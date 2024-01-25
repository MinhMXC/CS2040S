public class IntegerExamination {
	static class MyInteger {
		public int intState;

		MyInteger(int k) {
			intState = k;
		}

		public String toString() {
			return Integer.toString(intState);
		}
	}

	public static void main(String[] args) {
		int i = 7;
		MyInteger j = new MyInteger(7);
		MyInteger k = new MyInteger(7);

		addOne(i);
		myIntAddOne(j);
		myOtherIntAddOne(k);

		System.out.println("The final value of i back in main is " + i);
		System.out.println("The final value of j back in main is " + j);
		System.out.println("The final value of k back in main is " + k);
	}

	static public void addOne(int i) {
		i = i + 1;
		System.out.println("I am in addOne. The value of i is " + i);
	}

	static public void myIntAddOne(MyInteger j) {
		j.intState = j.intState + 1;
		// Analogy: Teaching tuition
		// You receive the address of the tutee, you go to his house and teach the tutee, tutee become smarter.
		System.out.println("I am in myIntAddOne. The value of j is " + j);
	}

	static public void myOtherIntAddOne(MyInteger k) {
		k = new MyInteger(k.intState + 1);
		// Analogy: Teaching tuition
		// You receive the address of the tutee, you replace the address with another smart person's address,
		// tutee does not become smarter.
		System.out.println("I am in myOtherIntAddOne. The value of k is " + k);
	}
}
