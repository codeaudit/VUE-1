package tufts.vue;

import java.util.Iterator;
import java.awt.geom.Rectangle2D;

public class LWSelection extends java.util.ArrayList
{
    static final boolean DEBUG_SELECTION = false;

    private java.util.List listeners = new java.util.ArrayList();
    private Rectangle2D bounds = null;
    
    public interface Listener extends java.util.EventListener {
        void selectionChanged(LWSelection selection);
    }

    public void addListener(Listener l)
    {
        listeners.add(l);
    }
    public void removeListener(Listener l)
    {
        listeners.remove(l);
    }

    private void notifyListeners()
    {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            Listener l = (Listener) i.next();
            l.selectionChanged(this);
        }
    }
       
    void setTo(LWComponent c)
    {
        clear0();
        add(c);
    }
    
    void setTo(Iterator i)
    {
        clear0();
        add(i);
    }
     
    void add(LWComponent c)
    {
        if (!c.isSelected()) {
            add0(c);
        } else
            if (DEBUG_SELECTION) System.out.println("addToSelection(already): " + c);
        notifyListeners();
    }
    
    public boolean add(Object o)
    {
        throw new RuntimeException("LWSelection can't add " + o.getClass() + ": " + o);
    }
    
    void add(Iterator i)
    {
        LWComponent c;
        while (i.hasNext()) {
            c = (LWComponent) i.next();
            add0(c);
        }
        notifyListeners();
    }
    
    private void add0(LWComponent c)
    {
        if (DEBUG_SELECTION) System.out.println("LWSelection adding " + c);
        c.setSelected(true);
        bounds = null;
        super.add(c);
    }
    
    public void remove(LWComponent c)
    {
        if (DEBUG_SELECTION) System.out.println("LWSelection removing " + c);
        c.setSelected(false);
        bounds = null;
        if (!super.remove(c))
            throw new RuntimeException("LWSelection remove: doesn't contain! " + c);
        notifyListeners();
    }
    
    public void clear()
    {
        if (clear0())
            notifyListeners();
    }

    private boolean clear0()
    {
        if (isEmpty())
            return false;
        if (DEBUG_SELECTION) System.out.println("LWSelection clear " + this);
        java.util.Iterator i = iterator();
        while (i.hasNext()) {
            LWComponent c = (LWComponent) i.next();
            c.setSelected(false);
        }
        bounds = null;
        super.clear();
        return true;
    }

    /** return bounds of map selection in map (not screen) coordinates */
    public Rectangle2D getBounds()
    {
        if (size() == 0)
            return null;
        //todo:not really safe to cache as we don't know if anything in has has moved?
        //if (bounds == null) {
            bounds = LWMap.getBounds(iterator());
            //System.out.println("COMPUTED SELECTION BOUNDS=" + bounds);
            //}
        return bounds;
    }

    void flushBounds()
    {
        bounds = null;
    }

    public boolean contains(float mapX, float mapY)
    {
        if (size() == 0)
            return false;
	return getBounds().contains(mapX, mapY);
    }

    public LWComponent first()
    {
        return (LWComponent) get(0);
    }
    
    public int countTypes(Class clazz)
    {
        int count = 0;
        Iterator i = iterator();
        while (i.hasNext()) {
            LWComponent c = (LWComponent) i.next();
            if (clazz.isInstance(c))
                count++;
        }
        return count;
    }
    
    public boolean allOfSameType()
    {
        LWComponent oc = null;
        Iterator i = iterator();
        while (i.hasNext()) {
            LWComponent c = (LWComponent) i.next();
            if (oc != null && oc.getClass() != c.getClass())
                return false;
            oc = c;
        }
        return true;
    }
    
    public boolean allHaveSameParent()
    {
        LWComponent oc = null;
        Iterator i = iterator();
        while (i.hasNext()) {
            LWComponent c = (LWComponent) i.next();
            if (oc != null && oc.getParent() != c.getParent())
                return false;
            oc = c;
        }
        return true;
    }
}
