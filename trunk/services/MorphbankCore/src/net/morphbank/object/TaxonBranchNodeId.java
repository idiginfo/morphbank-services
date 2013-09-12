package net.morphbank.object;

import java.io.Serializable;

public class TaxonBranchNodeId implements Serializable {

	private static final long serialVersionUID = 1L;

	int child;

	int tsn;

	public boolean equals(Object obj) {
		if (obj instanceof TaxonBranchNodeId) {
			TaxonBranchNodeId id = (TaxonBranchNodeId) obj;
			return child == id.child && tsn == id.tsn;
		}
		return false;
	}

	public int hashCode() {
		return super.hashCode();
	}
}
