/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2007 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/

package org.processmining.analysis.benchmark.metric;

import org.processmining.converting.PetriNetToHeuristicNetConverter;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.ui.Progress;
import org.processmining.mining.geneticmining.analysis.duplicates.TraceParsing;

/**
 * 
 * @author Ana Karla A. de Medeiros
 * @version 1.0
 */
public class BehavioralRecallMetric implements BenchmarkMetric {

	public BehavioralRecallMetric() {
	}

	/**
	 * Calculates the "Behavioral Recall" metric for a given mined model with
	 * respect to a log and a reference model.
	 * 
	 * @param model
	 *            The resulting Petri net generated by a mining algorithm.
	 * @param referenceLog
	 *            The log to be used during the calculation of the behavioral
	 *            recall value of the mined model.
	 * @param referenceModel
	 *            The Petri net used to measure behavioral recall of the mined
	 *            model.
	 * @param progress
	 *            Progress
	 * @return The behavioral recall value (<code>[0, 1]</code>) of the mined
	 *         model. If the behavioral recall value cannot be calculated, the
	 *         value <code>BenchmarkMetric.INVALID_MEASURE_VALUE</code> is
	 *         returned.
	 */
	public double measure(PetriNet model, LogReader referenceLog,
			PetriNet referenceModel, Progress progress) {

		// check precondition: no shared inputs for duplicate tasks
		if (model.hasDuplicatesWithSharedInputPlaces() == true
				|| referenceModel.hasDuplicatesWithSharedInputPlaces() == true) {
			return BenchmarkMetric.INVALID_MEASURE_VALUE;
		}

		try {
			HeuristicsNet HNmodel = new PetriNetToHeuristicNetConverter()
					.toHeuristicsNet(PetriNetToHeuristicNetConverter
							.removeUnnecessaryInvisibleTasksFromPetriNet((PetriNet) model
									.clone()));
			HeuristicsNet HNreferenceModel = new PetriNetToHeuristicNetConverter()
					.toHeuristicsNet(PetriNetToHeuristicNetConverter
							.removeUnnecessaryInvisibleTasksFromPetriNet((PetriNet) referenceModel
									.clone()));
			TraceParsing behavioralMetrics = new TraceParsing(referenceLog,
					HNreferenceModel, HNmodel);
			return behavioralMetrics.getRecall();
		} catch (Exception e) {
			System.err
					.println("BehavioralRecallMetric >>> Could not calculate the behavioral recall value!");
			e.printStackTrace();

		}
		return BenchmarkMetric.INVALID_MEASURE_VALUE;
	}

	/**
	 * 
	 * @return The name of this metric. Namely, "Behavioral Recall"
	 */
	public String name() {
		return "Behavioral Recall BR";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.analysis.benchmark.metric.BenchmarkMetric#description()
	 */
	public String description() {
		return "The metric <b>behavioral recall B<sub>R</sub></b> measures "
				+ "how much extra behavior the <i>reference</i> model allows for with respect "
				+ "to a given <i>mined</i> model and log. "
				+ "This metric is calculated by measuring the intersection between the set "
				+ "of enabled tasks that the	mined and reference models have at every moment "
				+ "of the log replay. This intersection is further weighed by the frequency "
				+ "of traces in the log. See also the metric <b>behavioral precision B<sub>P</sub></b>.";

	}

	/**
	 * This metric needs a reference log.
	 * 
	 * @return <code>true</code>
	 */
	public boolean needsReferenceLog() {
		return true;
	}

	/**
	 * This metric needs a reference model.
	 * 
	 * @return <code>true</code>
	 */
	public boolean needsReferenceModel() {
		return true;
	}
}
