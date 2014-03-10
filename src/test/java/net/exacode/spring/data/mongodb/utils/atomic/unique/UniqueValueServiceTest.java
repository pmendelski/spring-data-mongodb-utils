package net.exacode.spring.data.mongodb.utils.atomic.unique;

import static org.assertj.core.api.Assertions.assertThat;
import net.exacode.spring.data.mongodb.utils.IntegrationTestBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueValueServiceTest extends IntegrationTestBase {

	private static final Object[] VALUES_A = new Object[] { "A1", "A2" };

	private static final Object[] VALUES_B = new Object[] { "B1", "B2", "B3" };

	@Autowired
	private UniqueValueService UniqueValueService;

	@Test
	public void shouldClaimValue() {
		// when
		boolean claimed = UniqueValueService.claim(VALUES_A);

		// then
		assertThat(claimed).isTrue();
	}

	@Test
	public void shouldClaimValueAndRetrieve() {
		// when
		UniqueValue uniqueValue = UniqueValueService.claimAndRetrieve(VALUES_A);

		// then
		assertThat(uniqueValue).isNotNull();
		assertThat(uniqueValue.getValues()).isEqualTo(VALUES_A);
		assertThat(uniqueValue.getToken()).isNotNull();
	}

	@Test
	public void shouldNotClaimAlreadyClaimedValue() {
		// given
		UniqueValueService.claim(VALUES_A);

		// when
		boolean claimed = UniqueValueService.claim(VALUES_A);

		// then
		assertThat(claimed).isFalse();
	}

	@Test
	public void shouldClaimOnlyOneValue() {
		// given
		UniqueValueService.claim(VALUES_A);

		// when
		boolean claimedA = UniqueValueService.claim(VALUES_A);
		boolean claimedB = UniqueValueService.claim(VALUES_B);

		// then
		assertThat(claimedA).isFalse();
		assertThat(claimedB).isTrue();
	}

	@Test
	public void shouldConfirmClaimedValueByOwner() {
		// given
		Object token = UniqueValueService.claimAndRetrieve(VALUES_A).getToken();

		// when
		boolean claimed = UniqueValueService
				.isClaimedWithToken(token, VALUES_A);

		// then
		assertThat(claimed).isTrue();
	}

	@Test
	public void shouldDenyClaimedValueByOwner() {
		// given
		Object token = UniqueValueService.claimAndRetrieve(VALUES_A).getToken();
		token = token.toString() + "X";

		// when
		boolean claimed = UniqueValueService
				.isClaimedWithToken(token, VALUES_A);

		// then
		assertThat(claimed).isFalse();
	}

	@Test
	public void shouldConfirmClaimedValue() {
		// given
		UniqueValueService.claim(VALUES_A);

		// when
		boolean claimed = UniqueValueService.isClaimed(VALUES_A);

		// then
		assertThat(claimed).isTrue();
	}

	@Test
	public void shouldDenyClaimedValue() {
		// when
		boolean claimed = UniqueValueService.isClaimed(VALUES_A);

		// then
		assertThat(claimed).isFalse();
	}

}
