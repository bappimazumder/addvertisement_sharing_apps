package com.advertise;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Disabled
class AdvertiseApiApplicationTests {

	Calculator calculator = new Calculator();
	@Test
	void shouldAddTwoNumber() {
		int numberOne = 10;
		int numberTwo = 20;
		int expected = 30;
		int result = calculator.add(numberOne,numberTwo);
		assertThat(result).isEqualTo(expected);
	}

	class Calculator{
		int add ( int a , int b){
			return a+b;
		}
	}

}
