package com.gestankbratwurst.avarioncore.util.tuples;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

public class Pair <K, V>{
	
	public static <K, V> Pair<K, V> of(K left, V right) {
		return new Pair<K, V>(left, right);
	}
	
	public Pair(final K left, final V right) {
		this.left = left;
		this.right = right;
	}
	
	@Nullable
	@Getter @Setter
	private K left;
	
	@Nullable
	@Getter @Setter
	private V right;
	
	public boolean isLeftSet() {
		return left != null;
	}
	
	public boolean isRightSet() {
		return right != null;
	}
	
}
