import java.util.Random;
import java.util.HashMap;

/**
 * This is the main class for your Markov Model.
 *
 * Assume that the text will contain ASCII characters in the range [1,255].
 * ASCII character 0 (the NULL character) will be treated as a non-character.
 *
 * Any such NULL characters in the original text should be ignored.
 */

// Simple aggregate to store data in MarkovModel
// This will ignore OOP principles because I don't want boilerplate getter and setter
class Data {
	public String str;
	public int count;
	public HashMap<Character, Integer> map;

	public Data(String str) {
		this.str = str;
		this.count = 0;
		this.map = new HashMap<>();
	}

	public void put(char c) {
		map.put(c, map.getOrDefault(c, 0) + 1);
		count++;
	}

	public int getCharOrZero(char c) {
		return map.getOrDefault(c, 0);
	}
}

public class MarkovModel {
	private HashMap<String, Data> model;
	private int order;

	// Use this to generate random numbers as needed
	private Random generator = new Random();

	// This is a special symbol to indicate no character
	public static final char NOCHARACTER = (char) 0;

	/**
	 * Constructor for MarkovModel class.
	 *
	 * @param order the number of characters to identify for the Markov Model sequence
	 * @param seed the seed used by the random number generator
	 */
	public MarkovModel(int order, long seed) {
		// Initialize your class here
		this.model = new HashMap<>();
		this.order = order;

		// Initialize the random number generator
		generator.setSeed(seed);
	}

	/**
	 * Builds the Markov Model based on the specified text string.
	 */
	// Sliding window approach
	// O(n) runtime
	public void initializeText(String text) {
		// Clearing the old model
		model.clear();

		if (order >= text.length())
			return;

		int end = order - 1;

		// Initialize the string builder to the first kgram
		StringBuilder s = new StringBuilder();
		for (int i = 0; i <= end; i++) {
			s.append(text.charAt(i));
		}

		// Filling up model hashmap
		while (end != text.length() - 1) {
			Data data = model.get(s.toString());
			if (data == null) {
				data = new Data(s.toString());
				model.put(s.toString(), data);
			}

			data.put(text.charAt(end + 1));

			s.append(text.charAt(end + 1));
			s.deleteCharAt(0);
			end++;
		}
	}

	/**
	 * Returns the number of times the specified kgram appeared in the text.
	 */
	public int getFrequency(String kgram) {
		if (kgram.length() != order || model.get(kgram) == null)
			return 0;
		return model.get(kgram).count;
	}

	/**
	 * Returns the number of times the character c appears immediately after the specified kgram.
	 */
	public int getFrequency(String kgram, char c) {
		if (kgram.length() != order || model.get(kgram) == null)
			return 0;
		return model.get(kgram).getCharOrZero(c);
	}

	/**
	 * Generates the next character from the Markov Model.
	 * Return NOCHARACTER if the kgram is not in the table, or if there is no
	 * valid character following the kgram.
	 */
	// O(klogk) where k is the number of keys inside the hashmap for the particular kgram
	// Could have been O(k) if I used a 256-length array but that waste a lot of memory
	public char nextCharacter(String kgram) {
		Data data = model.get(kgram);
		if (data == null)
			return NOCHARACTER;

		float rand = generator.nextInt(data.count);

		int i = 0;
		// Because have to consider each character in ASCII Order
		Object[] keys = data.map.keySet().stream().sorted().toArray();

		for (Object o : keys) {
			Character c = (Character) o;
			// Guaranteed to not be 0
			int count = data.getCharOrZero(c);
			if (i <= rand && rand < i + count)
				return c;
			else
				i += count;
		}


		return NOCHARACTER;
	}
}
