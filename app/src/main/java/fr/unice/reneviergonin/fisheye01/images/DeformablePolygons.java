package fr.unice.reneviergonin.fisheye01.images;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.Random;

public class DeformablePolygons extends DeformableView {

    // nombre de polygones par ligne / colonne
    int nb = 20;

    ArrayList<MyPolygon> elements;
    ArrayList<MyPolygon> deformedElements;

    public DeformablePolygons (Context context, AttributeSet attrs) {
        super(context, attrs);

        // pour forcer la taille de la view
        // setDimensions(initialSize[0], initialSize[1]);
    }

    protected void derivePolygons () {
        elements = new ArrayList<MyPolygon>();

        // dimension dans laquelle s'inscrit un polygone
        float w = (originalSize[0] - marges * 2) / (nb * 2);
        float h = (originalSize[1] - marges * 2) / (nb * 2);

        int tiers = nb / 3;
        int sixieme = nb / 6;
        int deuxtiers = 2 * nb / 3;
        int troisquarts = 3 * nb / 4;
        int lol1 = new Random().nextInt(deuxtiers) + tiers;
        int lol2 = new Random().nextInt(troisquarts) + sixieme;
        int lol3 = new Random().nextInt(troisquarts) + tiers;
        int lol4 = new Random().nextInt(deuxtiers) + sixieme;

        float pasW = w * 7 / 24; // entre 1/4 et 1/3...
        float pasH = h * 7 / 24;

        // création de tous les polygones
        for (int i = 0; i < nb; i++) {
            for (int j = 0; j < nb; j++) {
                MyPolygon p = new MyPolygon();
                float dx = w * 2 * i + marges;
                float dy = h * 2 * j + marges;
                float delta = 0;
                float delta2 = 0;

                /*
                // pour faire des formes différentes de temps en temps
				if ((j %3 ) == 1) delta = h/2;
				if ((i %2 ) == 1) delta2 = w/2;

				if ((delta2 > 0) && (delta > 0))
				{
					delta2 = delta2/2;
					delta = delta/2;
				}
				*/

                // ajout des points constituants les polygones
                if (i == lol1 && j == lol3) {
                    p.addPoint(dx, dy + pasH);
                    p.addPoint(dx + pasW, dy);
                } else {
                    p.addPoint(dx, dy);
                }

                p.addPoint(dx + w / 2, dy + delta);
                p.addPoint(dx + w, dy);
                p.addPoint(dx + w - delta2, dy + h / 2);

                if (i == lol2 && j == lol4) {
                    p.addPoint(dx + w, dy - pasH + h);
                    p.addPoint(dx - pasW + w, dy + h);
                } else {
                    p.addPoint(dx + w, dy + h);
                }

                p.addPoint(dx + w / 2, dy + h - delta);
                p.addPoint(dx, dy + h);
                p.addPoint(dx + delta2, dy + h / 2);

                p.color = Color.BLACK;

                /*
                if ((i == tiers) && (j==sixieme))
                {
                    p.color = Color.RED;
                }
                else if ((i == troisquarts) && (j==deuxtiers))
                {
                    p.color = Color.BLUE;
                }
                else {
                    int coul = (i+j)*255/(2*nb);
                    p.color = Color.rgb(coul, coul, coul);
                }
                */

                elements.add(p);
            }
        }

    }


    /*
    // pour forcer la taille de la view
	public void setDimensions(int width, int height)
	{
		super.setDimensions(width, height);

		derivePolygons();
		processDeform();
	}
    */


    @Override
    protected void calculateNewImage () {
        if (mydeform == null) return;
        if (elements == null) return;
        deformedElements = new ArrayList<MyPolygon>();

        double restrictedDistance = mydeform.getUnchangedX();

        for (MyPolygon p : elements) {

            MyPolygon dp = new MyPolygon();

            for (int pt = 0; pt < p.getNbPoints(); pt++) {
                float i = p.xPoint(pt);
                float j = p.yPoint(pt);

                double dist = Math.sqrt((i - centreX) * (i - centreX) + (j - centreY) * (j - centreY));

                double scale = 0;
                if (dist > 1) {
                    // vérifier les bornes (ArrayIndexOutOfBoundsException)
                    if (dist > newx.length) return; // on est hors de l'image, on ne doit pas réagir
                    else scale = newx[(int) dist] / dist;
                }

                int ni = (int) (centreX + (i - centreX) * scale);
                int nj = (int) (centreY + (j - centreY) * scale);

                if (partiallyDeformed) {
                    //if  (dist < mydeform.r)
                    if (dist < restrictedDistance) {
                        dp.addPoint(ni, nj);
                    } else dp.addPoint(i, j);

                } else {
                    dp.addPoint(ni, nj);
                }
            }
            dp.color = p.color;
            deformedElements.add(dp);

        }

    }

    @Override
    protected void initialize () {
        derivePolygons();
        processDeform();
    }

    @Override
    protected void update () {
        processDeform();
    }

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        paint.setStrokeWidth(3);
        // paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    Paint paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);

    {
        paintBackground.setStrokeWidth(3);
        paintBackground.setColor(0x99990000);
    }

    public void onDraw (Canvas g) {
        super.onDraw(g);
        g.drawColor(Color.WHITE);

        g.drawRect(0, 0, mySize[0], mySize[1], paintBackground);

        ArrayList<MyPolygon> elts;

        if (mydeform == null) elts = elements;
        else elts = deformedElements;


        for (MyPolygon p : elts) {
            paint.setColor(p.color);
            /*
            // plus lent que de faire la boucle ?
            Path poly = p.getPath(marges);
            g.drawPath(poly, paint);
            /*  */
            float[] pts = p.getPoints();
            for (int i = 0; i < pts.length - 3; i = i + 2) {
                g.drawLine(pts[i] + marges, pts[i + 1] + marges, pts[i + 2] + marges, pts[i + 3] + marges, paint);
            }
            g.drawLine(pts[pts.length - 2] + marges, pts[pts.length - 1] + marges, pts[0] + marges, pts[1] + marges, paint);
            /* */
        }

    }


}
