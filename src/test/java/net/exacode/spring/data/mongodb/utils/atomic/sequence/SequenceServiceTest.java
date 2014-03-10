package net.exacode.spring.data.mongodb.utils.atomic.sequence;

import static org.assertj.core.api.Assertions.assertThat;
import net.exacode.spring.data.mongodb.utils.IntegrationTestBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SequenceServiceTest extends IntegrationTestBase {

	private static final String SEQ_A = "SEQ-A";

	private static final String SEQ_B = "SEQ-B";

	@Autowired
	private SequenceService sequenceService;

	@Test
	public void shouldStartNewSequence() {
		// when
		long seq = sequenceService.getNextSequence(SEQ_A);

		// then
		assertThat(seq).isEqualTo(1);
	}

	@Test
	public void shouldIncerementSequence() {
		// given
		sequenceService.getNextSequence(SEQ_A);

		// when
		long seq = sequenceService.getNextSequence(SEQ_A);

		// then
		assertThat(seq).isEqualTo(2);
	}

	@Test
	public void shouldIncerementSequencesSeparately() {
		// given
		sequenceService.getNextSequence(SEQ_A);

		// when
		long seqA = sequenceService.getNextSequence(SEQ_A);
		long seqB = sequenceService.getNextSequence(SEQ_B);

		// then
		assertThat(seqA).isEqualTo(2);
		assertThat(seqB).isEqualTo(1);
	}
}
