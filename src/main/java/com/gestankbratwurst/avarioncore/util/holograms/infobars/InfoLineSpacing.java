package com.gestankbratwurst.avarioncore.util.holograms.infobars;

public enum InfoLineSpacing {
	
	SMALL(0.12),
	MEDIUM(0.25),
	LARGE(0.45);
	
	private final double spacingValue;
	
	private InfoLineSpacing(double spacingValue) {
		this.spacingValue = spacingValue;
	}
	
	public double getSpacingValue() {
		return this.spacingValue;
	}
	
}
