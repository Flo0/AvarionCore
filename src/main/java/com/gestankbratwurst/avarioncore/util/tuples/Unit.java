package com.gestankbratwurst.avarioncore.util.tuples;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

public class Unit <T> {
	
	public static <T> Unit<T> of(final T value) {
		return new Unit<T>(value);
	}
	
	public Unit(final T value) {
		this.value = value;
	}
	
	@Nullable
	@Getter @Setter
	private T value;
	
	public boolean isPresent() {
		return value != null;
	}
	
}
