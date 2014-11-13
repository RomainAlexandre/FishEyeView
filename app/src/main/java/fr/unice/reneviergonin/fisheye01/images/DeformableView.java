package fr.unice.reneviergonin.fisheye01.images;


//import java.awt.event.*;
//import java.awt.image.BufferedImage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.Observable;
import java.util.Observer;

import fr.unice.reneviergonin.fisheye01.deform.AbstractFormula;

public abstract class DeformableView extends View implements Observer {

    protected final static int[] initialSize = {600, 600};

    protected static int marges = 20;


    protected int[] mySize = new int[2];
    protected int[] originalSize = new int[2];
    protected AbstractFormula mydeform;
    protected int maxdistance;


    protected int centreX;
    protected int centreY;

    protected int maxx, minx;

    protected int[] newx;

    protected double scaleX = 1;
    protected double scaleY = 1;

    protected boolean partiallyDeformed = true;


    protected Context c;


    /**
     * @return the partiallyDeformed
     */
    public boolean isPartiallyDeformed () {
        return partiallyDeformed;
    }


    /**
     * @param partiallyDeformed the partiallyDeformed to set
     */
    public void setPartiallyDeformed (boolean partiallyDeformed) {
        this.partiallyDeformed = partiallyDeformed;
        processDeform();
    }


    public DeformableView (Context context, AttributeSet attrs) {
        super(context, attrs);

        c = context;
    }



    /*
    // pour forcer la taille de la view...
    public void setDimensions(int width, int height) {
        mySize[0] = (int) ((width + marges * 2) / scaleX);
        mySize[1] = (int) ((height + marges * 2) / scaleY);
        originalSize[0] = width;
        originalSize[1] = height;
        //		setSize(mySize);
        //		setPreferredSize(mySize);

        centreX = (int) (originalSize[0] / (scaleX * 2));
        centreY = (int) (originalSize[1] / (scaleY * 2));

        maxx = width;
        minx = 0;
        newx = new int[originalSize[0]];


        maxdistance = (int) Math.sqrt(width * width + height * height);
        newx = new int[maxdistance];

    }
    */

    public AbstractFormula getDeformation () {
        return mydeform;
    }

    public boolean isDeformed () {
        return (mydeform != null);
    }


    public void setDeformation (AbstractFormula af) {

        if (mydeform != null) mydeform.deleteObserver(this);
        mydeform = af;
        if (af != null) {
            af.addObserver(this);
            processDeform();
        }
    }


    public void update (Observable arg0, Object arg1) {
        processDeform();
        invalidate();
    }


    protected void processDeform () {

        if (mydeform == null) return;

        minx = originalSize[0];
        maxx = 0;

        for (int i = 0; i < maxdistance; i++) {
            double x = mydeform.deform(i);
            newx[i] = (int) x;

            if (x > maxx) maxx = (int) x;
            if (x < minx) minx = (int) x;
        }

        calculateNewImage();
    }

    protected abstract void calculateNewImage ();


    public String toString () {
        return getClass().getCanonicalName();
    }


    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        /*
        // pour forcer la dimension de la view
        setMeasuredDimension(mySize[0], mySize[1]);
        */

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getWidth();
        int height = getHeight();

        mySize[0] = (int) ((width + marges * 2) / scaleX);
        mySize[1] = (int) ((height + marges * 2) / scaleY);
        originalSize[0] = width;
        originalSize[1] = height;

        centreX = (int) (originalSize[0] / (scaleX * 2));
        centreY = (int) (originalSize[1] / (scaleY * 2));

        maxx = width;
        minx = 0;
        newx = new int[originalSize[0]];

        maxdistance = (int) Math.sqrt(width * width + height * height);
        newx = new int[maxdistance];

        initialize(1);
        invalidate();
    }

    protected abstract void initialize(int i);
    protected abstract void update();

    public void setCenter (int centerX, int centerY) {
        this.centreX = centerX;
        this.centreY = centerY;

        update();
        invalidate();
    }

    public void resetCenter(int i) {
        setCenter((int) (originalSize[0] / (scaleX * 2)),
                (int) (originalSize[1] / (int) (scaleY * 2)));
        initialize(i);
        invalidate();
    }

    public int getCentreX () {
        return centreX;
    }

    public int getCentreY () {
        return centreY;
    }
}