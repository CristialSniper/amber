package haven.livestock;


import haven.*;
import haven.Label;

import java.awt.*;
import java.util.Map;

public class DetailsWdg extends Widget {
    public final static int HEIGHT = 25;
    private final Coord sepStart = new Coord(0, HEIGHT);
    private final Coord sepEnd = new Coord(800 - 40 - 11, HEIGHT);
    public Animal animal;
    private boolean hover = false;
    private static final Text.Foundry delfnd = new Text.Foundry(Text.sans.deriveFont(Font.BOLD), 16);

    public DetailsWdg(Animal animal) {
        this.animal = animal;

        add(new Img(animal.getAvatar()), Coord.z);

        int offx = LivestockManager.COLUMN_TITLE_X - LivestockManager.ENTRY_X;
        for (Map.Entry<String, Integer> entry : animal.entrySet()) {
            Integer val = entry.getValue();
            if (val == null)
                continue;

            String key = entry.getKey();
            Column col = animal.getColumns().get(key);

            String valStr = val.toString();
            if (key.equals(Resource.getLocString(Resource.BUNDLE_LABEL, "Meat quality:")) ||
                key.equals(Resource.getLocString(Resource.BUNDLE_LABEL, "Milk quality:")) ||
                key.equals(Resource.getLocString(Resource.BUNDLE_LABEL, "Hide quality:")) ||
                key.equals(Resource.getLocString(Resource.BUNDLE_LABEL, "Wool quality:")) ||
                key.equals(Resource.getLocString(Resource.BUNDLE_LABEL, "Endurance:")))
                valStr += "%";

            Label lbl = new Label(valStr, Text.std);
            add(lbl, new Coord(col.x + offx, 5));
        }

        Label del = new Label("\u2718", delfnd, Color.RED, true) {
            @Override
            public boolean mousedown(Coord c, int button) {
                delete();
                return true;
            }
        };
        Column col = animal.getColumns().get("X");
        add(del, new Coord(col.x + offx, 3));
    }

    @Override
    public void draw(GOut g) {
        g.chcolor(255, 255, 255, 128);
        g.line(sepStart, sepEnd, 1);

        if (hover) {
            g.chcolor(255, 255, 255, 40);
            g.frect(Coord.z, sz);
        }

        g.chcolor();

        super.draw(g, true);
    }

    @Override
    public boolean mousedown(Coord c, int button) {
        Gob gob = gameui().map.glob.oc.getgob(animal.gobid);
        if (gob != null) {
            gob.delattr(GobHighlight.class);
            gob.setattr(new GobHighlight(gob));
        }
        return super.mousedown(c, button);
    }

    @Override
    public void mousemove(Coord c) {
        hover = c.x > 0 && c.x < sz.x && c.y > 0 && c.y < sz.y;
        super.mousemove(c);
    }

    public void delete() {
        reqdestroy();
        int y = this.c.y;
        for (Widget child = parent.lchild; child != null; child = child.prev) {
            if (child instanceof DetailsWdg && child.c.y > y)
                child.c.y -= HEIGHT;
        }

        ((LivestockManager.Panel)parent.parent.parent).delete(animal);

        ((Scrollport.Scrollcont) parent).update();
    }
}
