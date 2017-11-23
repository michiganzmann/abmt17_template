package ch.ethz.matsim.courses.abmt17_template.scoring;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.SumScoringFunction.AgentStuckScoring;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import abmt17.scoring.ABMTScoringFunctionFactory;
import ch.ethz.matsim.av.config.AVConfig;
import ch.ethz.matsim.av.framework.AVModule;
import ch.ethz.matsim.av.scoring.AVScoringFunction;

@Singleton
public class AVScoringFunctionFactoryForABMT implements ScoringFunctionFactory {
	@Inject
	ABMTScoringFunctionFactory delegate;

	@Inject
	AVConfig avConfig;

	@Inject
	PlanCalcScoreConfigGroup scoringConfig;

	@Override
	public ScoringFunction createNewScoringFunction(Person person) {
		/*
		 * - Use the default ABMTScoringFunctionFactory to create the base scoring
		 * function
		 * 
		 * - Add the AV pricing scoring (do not score prices by the default MATSim
		 * mechanism!)
		 * 
		 * - Add a new stuck scoring, because all activity scoring in the scenario is
		 * set to zero. This leads to a stuck score of "0", so we need to do it manually
		 * here.
		 */

		SumScoringFunction scoringFunction = (SumScoringFunction) delegate.createNewScoringFunction(person);
		scoringFunction
				.addScoringFunction(new AVScoringFunction(avConfig, person, scoringConfig.getMarginalUtilityOfMoney(),
						scoringConfig.getModes().get(AVModule.AV_MODE).getMarginalUtilityOfTraveling() / 3600.0));

		scoringFunction.addScoringFunction(new AgentStuckScoring() {
			private boolean isStuck = false;

			@Override
			public void finish() {
			}

			@Override
			public double getScore() {
				return isStuck ? -100.0 : 0.0;
			}

			@Override
			public void agentStuck(double time) {
				isStuck = true;
			}
		});

		return scoringFunction;
	}
}
