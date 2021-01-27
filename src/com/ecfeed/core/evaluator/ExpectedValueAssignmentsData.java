package com.ecfeed.core.evaluator;

import java.util.ArrayList;
import java.util.List;

import com.ecfeed.core.model.AssignmentStatement;
import com.ecfeed.core.utils.Pair;

public class ExpectedValueAssignmentsData {

	//Integer is the variable of pre-condition enforcing postcondition ExpectedValueStatement
	private List<Pair<Integer, AssignmentStatement>> fExpectedValueAssignments;

	public ExpectedValueAssignmentsData() {
		fExpectedValueAssignments = new ArrayList<>();
	}

	public List<Pair<Integer, AssignmentStatement>> getList() {
		return fExpectedValueAssignments;
	}

	public void add(Pair<Integer, AssignmentStatement> pair) {
		fExpectedValueAssignments.add(pair);
	}

}
