package tattoo.gogo.app.gogo_android.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tattoo.gogo.app.gogo_android.model.Tattoo;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Tattoo> ITEMS = new ArrayList<Tattoo>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Tattoo> ITEM_MAP = new HashMap<String, Tattoo>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Tattoo item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getShortName(), item);
    }

    private static Tattoo createDummyItem(int position) {
        return new Tattoo();
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
}
