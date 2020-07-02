package com.gestankbratwurst.avarioncore.util.functional;

import java.util.function.Function;

public interface TripleFunction <P1, P2, P3, R> {
	
	R apply(P1 paramOne, P2 paramTwo, P3 paramThree);
	Function<R, ?> andThen();
	
}
