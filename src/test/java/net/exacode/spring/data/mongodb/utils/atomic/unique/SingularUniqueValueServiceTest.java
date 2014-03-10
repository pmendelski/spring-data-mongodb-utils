package net.exacode.spring.data.mongodb.utils.atomic.unique;

import static org.assertj.core.api.Assertions.assertThat;
import net.exacode.spring.data.mongodb.utils.IntegrationTestBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SingularUniqueValueServiceTest extends IntegrationTestBase {

	private static final String VALUE_A = "VALUE-A";

	private static final String VALUE_B = "VALUE-B";

	@Autowired
	private UniqueValueService UniqueValueService;

	@Test
	public void shouldClaimValue() {
		// when
		boolean claimed = UniqueValueService.claim(VALUE_A);

		// then
		assertThat(claimed).isTrue();
	}

	@Test
	public void shouldClaimValueAndRetrieve() {
		// when
		UniqueValue uniqueValue = UniqueValueService.claimAndRetrieve(VALUE_A);

		// then
		assertThat(uniqueValue).isNotNull();
		assertThat(uniqueValue.getValues()).isEqualTo(new Object[] { VALUE_A });
		assertThat(uniqueValue.getToken()).isNotNull();
	}

	@Test
	public void shouldNotClaimAlreadyClaimedValue() {
		// given
		UniqueValueService.claim(VALUE_A);

		// when
		boolean claimed = UniqueValueService.claim(VALUE_A);

		// then
		assertThat(claimed).isFalse();
	}

	@Test
	public void shouldClaimOnlyOneValue() {
		// given
		UniqueValueService.claim(VALUE_A);

		// when
		boolean claimedA = UniqueValueService.claim(VALUE_A);
		boolean claimedB = UniqueValueService.claim(VALUE_B);

		// then
		assertThat(claimedA).isFalse();
		assertThat(claimedB).isTrue();
	}

	@Test
	public void shouldConfirmClaimedValueByOwner() {
		// given
		Object token = UniqueValueService.claimAndRetrieve(VALUE_A).getToken();

		// when
		boolean claimed = UniqueValueService.isClaimedWithToken(token, VALUE_A);

		// then
		assertThat(claimed).isTrue();
	}

	@Test
	public void shouldDenyClaimedValueByOwner() {
		// given
		Object token = UniqueValueService.claimAndRetrieve(VALUE_A).getToken();
		token = token.toString() + "X";

		// when
		boolean claimed = UniqueValueService.isClaimedWithToken(token, VALUE_A);

		// then
		assertThat(claimed).isFalse();
	}

	@Test
	public void shouldConfirmClaimedValue() {
		// given
		UniqueValueService.claim(VALUE_A);

		// when
		boolean claimed = UniqueValueService.isClaimed(VALUE_A);

		// then
		assertThat(claimed).isTrue();
	}

	@Test
	public void shouldDenyClaimedValue() {
		// when
		boolean claimed = UniqueValueService.isClaimed(VALUE_A);

		// then
		assertThat(claimed).isFalse();
	}

}
