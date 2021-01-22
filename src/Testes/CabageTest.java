package Testes;

import pt.iul.ista.poo.farm.objects.Cabbage;
import pt.iul.ista.poo.utils.Point2D;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


public class CabageTest {

	Cabbage c1,c2,c3,c4;
	
	@BeforeEach
	public void setUp() {
		c1 = new Cabbage(new Point2D(2,2));
		c2 = new Cabbage(new Point2D(2,2));
		c3 = new Cabbage(new Point2D(5,2));
	}

	@Test
	public void testNullAndNotNull() {
		assertNotNull(c1);
		assertNotNull(c2);
		assertNotNull(c3);
		assertNull(c4);
	}
	
	@Test
	public void testToString() {
		assertEquals(c1.toString(),"A couve encontra-se na posição (2,2)");
		assertEquals(c2.toString(),"A couve encontra-se na posição (2,2)");
		assertEquals(c3.toString(),"A couve encontra-se na posição (5,2)");
	}
}
