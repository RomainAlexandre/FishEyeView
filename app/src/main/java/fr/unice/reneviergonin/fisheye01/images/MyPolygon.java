package fr.unice.reneviergonin.fisheye01.images;

import android.graphics.Path;


public class MyPolygon {

    // ArrayList<Float> points  = new ArrayList<Float>() ;
    public int color;
    float[] res ;
    int length = 0;

    public void addPoint(float dx, float dy) {
        // TODO Auto-generated method stub
        // points.add(dx);
        // points.add(dy);

        length += 1;

        // float [] newres = new float[points.size()];
        float [] newres = new float[length*2];
        if (res != null)
        {
            System.arraycopy(res, 0, newres, 0, res.length);
            newres[res.length] = dx;
            newres[res.length+1] = dy;
        }
        else
        {
            newres[0] = dx;
            newres[1] = dy;
        }
        res = newres;

    }

    public int getNbPoints() {
        // TODO Auto-generated method stub
        // return points.size()/2;
        return length;
    }


    public float xPoint(int index)
    {
        // return points.get(index*2);
        return res[index*2];
    }

    public float yPoint(int index)
    {
        // return points.get(index*2+1);
        return res[index*2+1];
    }

    public float[] getPoints() {
        return res;
    }


    public Path getPath(int marges) {
        Path result = null ;
        if (res.length > 2) {
            result = new Path();
            int beforelast = res.length-1;
            result.moveTo(res[0]+marges, res[1]+marges);
            for(int i = 2; i < beforelast; i=i+2) {
                result.lineTo(res[i]+marges, res[i+1]+marges);
            }
            result.close();
        }
        return result;
    }

}
