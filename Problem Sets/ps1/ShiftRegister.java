///////////////////////////////////
// This is the main shift register class.
// Notice that it implements the ILFShiftRegister interface.
// You will need to fill in the functionality.
///////////////////////////////////

/**
 * class ShiftRegister
 * @author
 * Description: implements the ILFShiftRegister interface.
 */
public class ShiftRegister implements ILFShiftRegister {
    ///////////////////////////////////
    // Create your class variables here
    ///////////////////////////////////
    int[] m_seed;
    int m_tap;

    ///////////////////////////////////
    // Create your constructor here:
    ///////////////////////////////////
    ShiftRegister(int size, int tap) {
        m_seed = new int[size];
        m_tap = tap;
    }

    ///////////////////////////////////
    // Create your class methods here:
    ///////////////////////////////////
    /**
     * setSeed
     * @param seed
     * Description:
     */
    @Override
    public void setSeed(int[] seed) {
        // Verify length
        if (seed.length < m_seed.length || m_seed.length < seed.length) {
            return;
        }

        // Verify valid seed
        for (int i = 0; i < seed.length; i++)
            if (seed[i] != 1 && seed[i] != 0)
                return;

        // Copy seed
        for (int i = 0; i < seed.length; i++)
            m_seed[i] = seed[i];
    }

    /**
     * shift
     * @return
     * Description:
     */
    @Override
    public int shift() {
        int feedback = m_seed[m_seed.length - 1] ^ m_seed[m_tap];
        for (int i = m_seed.length - 1; i > 0; i--)
            m_seed[i] = m_seed[i - 1];
        m_seed[0] = feedback;
        return feedback;
    }

    /**
     * generate
     * @param k
     * @return
     * Description:
     */
    @Override
    public int generate(int k) {
        int[] returned = new int[k];
        for (int i = 0; i < k; i++)
            returned[i] = shift();
        return toDecimal(returned);
    }

    /**
     * Returns the integer representation for a binary int array.
     * @param array
     * @return
     */
    private static int toDecimal(int[] array) {
        // This assumes the largest bit is array[0]
        int ans = 0;
        for (int i = 0; i < array.length; i++)
            ans += (int) (array[i] * Math.pow(2, array.length - 1 - i));
        return ans;
    }
}
