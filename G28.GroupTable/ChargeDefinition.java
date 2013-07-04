/**
 * The definition of a charge
 */
public class ChargeDefinition {

	private Long encounterId;
	private Long chargeId;
	private String chargeCode;
	private String chargeDesc;

	public ChargeDefinition(Long encounterId, Long chargeId, String chargeCode, String chargeDesc) {
		this.encounterId = encounterId;
		this.chargeId = chargeId;
		this.chargeCode = chargeCode;
		this.chargeDesc = chargeDesc;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj != null && obj instanceof ChargeDefinition) {
			ChargeDefinition v = (ChargeDefinition) obj;
			return v.getChargeCode().equalsIgnoreCase(this.getChargeCode())
					&& v.getChargeId().compareTo(this.getChargeId()) == 0
					&& v.getEncounterId().compareTo(this.getEncounterId()) == 0;
		}
		return false;
	}

	public String getChargeCode() {
		return chargeCode;
	}

	public String getChargeDesc() {
		return chargeDesc;
	}

	public Long getChargeId() {
		return chargeId;
	}

	/*
	 * Need to override these because when reselecting nodes, inside the tree model (Sun and JIDE's code) there is a
	 * hashtable that is used
	 */

	public Long getEncounterId() {
		return encounterId;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (this.getChargeId() == null ? 0 : this.getChargeId().hashCode());
		result = 31 * result + (this.getEncounterId() == null ? 0 : this.getEncounterId().hashCode());
		result = 31 * result + (this.getChargeCode() == null ? 0 : this.getChargeCode().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return this.getChargeCode();
	}

}