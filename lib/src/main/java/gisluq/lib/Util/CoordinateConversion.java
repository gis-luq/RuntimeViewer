package gisluq.lib.Util;

/**
 * 经纬度坐标与高斯投影坐标之间转换
 * Created by luq on 2016/9/11.
 */
public class CoordinateConversion {

    /**
     *  由高斯投影坐标反算成经纬度
     *  @param X 高斯坐标 X
     *  @param Y 高斯坐标 Y
     *  return (double *longitude, double *latitude)
     */
    public static double[] GaussToBL(double X, double Y)
    {
        int ProjNo; int ZoneWide; ////带宽
        double[] output = new double[2];
        double longitude1,latitude1, longitude0, X0,Y0, xval,yval;//latitude0,
        double e1,e2,f,a, ee, NN, T,C, M, D,R,u,fai, iPI;
        iPI = 0.0174532925199433; ////3.1415926535898/180.0;
        //a = 6378245.0; f = 1.0/298.3; //54年北京坐标系参数
        a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ZoneWide = 6; ////6度带宽
        ProjNo = (int)(X/1000000L) ; //查找带号
        longitude0 = (ProjNo-1) * ZoneWide + ZoneWide / 2;
        longitude0 = longitude0 * iPI ; //中央经线

        X0 = ProjNo*1000000L+500000L;
        Y0 = 0;
        xval = X-X0; yval = Y-Y0; //带内大地坐标
        e2 = 2*f-f*f;
        e1 = (1.0- Math.sqrt(1-e2))/(1.0+ Math.sqrt(1-e2));
        ee = e2/(1-e2);
        M = yval;
        u = M/(a*(1-e2/4-3*e2*e2/64-5*e2*e2*e2/256));
        fai = u+(3*e1/2-27*e1*e1*e1/32)* Math.sin(2*u)+(21*e1*e1/16-55*e1*e1*e1*e1/32)* Math.sin(
                4*u)
                +(151*e1*e1*e1/96)* Math.sin(6*u)+(1097*e1*e1*e1*e1/512)* Math.sin(8*u);
        C = ee* Math.cos(fai)* Math.cos(fai);
        T = Math.tan(fai)* Math.tan(fai);
        NN = a/ Math.sqrt(1.0-e2* Math.sin(fai)* Math.sin(fai));
        R = a*(1-e2)/ Math.sqrt((1-e2* Math.sin(fai)* Math.sin(fai))*(1-e2* Math.sin(fai)* Math.sin(fai))*(1-e2* Math.sin
                (fai)* Math.sin(fai)));
        D = xval/NN;
        //计算经度(Longitude) 纬度(Latitude)
        longitude1 = longitude0+(D-(1+2*T+C)*D*D*D/6+(5-2*C+28*T-3*C*C+8*ee+24*T*T)*D
                *D*D*D*D/120)/ Math.cos(fai);
        latitude1 = fai -(NN* Math.tan(fai)/R)*(D*D/2-(5+3*T+10*C-4*C*C-9*ee)*D*D*D*D/24
                +(61+90*T+298*C+45*T*T-256*ee-3*C*C)*D*D*D*D*D*D/720);
        //转换为度 DD
        output[0] = longitude1 / iPI;
        output[1] = latitude1 / iPI;
        return output;
    }


    /**
     * 由经纬度反算成高斯投影坐标
     * @param longitude
     * @param latitude
     */
    public static double[] GaussToBLToGauss(double longitude, double latitude)
    {
        double[] output = new double[2];
        int ProjNo=0; int ZoneWide; ////带宽
        double longitude1,latitude1, longitude0,latitude0, X0,Y0, xval,yval;
        double a,f, e2,ee, NN, T,C,A, M, iPI;
        iPI = 0.0174532925199433; ////3.1415926535898/180.0;
        ZoneWide = 6; ////6度带宽
        a=6378245.0; f=1.0/298.3; //54年北京坐标系参数
        ////a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ProjNo = (int)(longitude / ZoneWide) ;
        longitude0 = ProjNo * ZoneWide + ZoneWide / 2;
        longitude0 = longitude0 * iPI ;
        latitude0 = 0;
        System.out.println(latitude0);
        longitude1 = longitude * iPI ; //经度转换为弧度
        latitude1 = latitude * iPI ; //纬度转换为弧度
        e2=2*f-f*f;
        ee=e2*(1.0-e2);
        NN=a/ Math.sqrt(1.0-e2* Math.sin(latitude1)* Math.sin(latitude1));
        T= Math.tan(latitude1)* Math.tan(latitude1);
        C=ee* Math.cos(latitude1)* Math.cos(latitude1);
        A=(longitude1-longitude0)* Math.cos(latitude1);
        M=a*((1-e2/4-3*e2*e2/64-5*e2*e2*e2/256)*latitude1-(3*e2/8+3*e2*e2/32+45*e2*e2
                *e2/1024)* Math.sin(2*latitude1)
                +(15*e2*e2/256+45*e2*e2*e2/1024)* Math.sin(4*latitude1)-(35*e2*e2*e2/3072)* Math.sin(6*latitude1));
        xval = NN*(A+(1-T+C)*A*A*A/6+(5-18*T+T*T+72*C-58*ee)*A*A*A*A*A/120);
        yval = M+NN* Math.tan(latitude1)*(A*A/2+(5-T+9*C+4*C*C)*A*A*A*A/24
                +(61-58*T+T*T+600*C-330*ee)*A*A*A*A*A*A/720);
        X0 = 1000000L*(ProjNo+1)+500000L;
        Y0 = 0;
        xval = xval+X0; yval = yval+Y0;
        output[0] = xval;
        output[1] = yval;
       return output;
    }


    /**
     * 经纬度转Wev墨卡托
     * @param longitude 经度
     * @param latitude 纬度
     */
    public static double[] lonLat2WebMercator(double longitude, double latitude)
    {
        double[] output = new double[2];
        double x = longitude *20037508.34/180;
        double y = Math.log(Math.tan((90+latitude)* Math.PI/360))/(Math.PI/180);
        y = y *20037508.34/180;
        output[0] = x;
        output[1] = y;
        return output ;
    }

    /**
     * Web墨卡托转经纬度
     * @param pX 平面坐标 X
     * @param pY 平面坐标 Y
     */

    public static double[] WebMercator2lonLat(double pX, double pY)
    {
        double[] output = new double[2];
        double x = pX/20037508.34*180;
        double y = pY/20037508.34*180;
        y= 180/ Math.PI*(2* Math.atan(Math.exp(y* Math.PI/180))- Math.PI/2);
        output[0] = x;
        output[1] = y;
        return output;
    }
}
