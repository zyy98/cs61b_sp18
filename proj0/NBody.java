public class NBody {
    public static double readRadius(String filename) {
        In in = new In(filename);
        in.readInt();
        double secondItemInFile = in.readDouble();
        return secondItemInFile;
    }

    public static Planet[] readPlanets(String filename) {
        In in = new In(filename);
        int num = in.readInt();
        in.readDouble();
        Planet[] plist = new Planet[num];
        for (int i = 0; i < num; i += 1) {
            double xxpos = in.readDouble();
            double yypos = in.readDouble();
            double xxvel = in.readDouble();
            double yyvel = in.readDouble();
            double mass = in.readDouble();
            String file = in.readString();
            plist[i] = new Planet(xxpos, yypos, xxvel, yyvel, mass, file);
        }
        return plist;
    }

    public static void main(String[] args) {
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        double radius = readRadius(filename);
        Planet[] plist = readPlanets(filename);

        StdDraw.setScale(-radius, radius);
        StdDraw.clear();
        StdDraw.picture(0,0,"./images/starfield.jpg");

        for (Planet p : plist) {
            p.draw();
        }

        StdDraw.enableDoubleBuffering();
        for (double time = 0; time <= T; time += dt){
            double[] xForces = new double[plist.length];
            double[] yForces = new double[plist.length];
            for (int i = 0; i < plist.length; i += 1) {
                xForces[i] = plist[i].calcNetForceExertedByX(plist);
                yForces[i] = plist[i].calcNetForceExertedByY(plist);
            }

            for (int i = 0; i < plist.length; i += 1) {
                plist[i].update(dt,xForces[i],yForces[i]);
            }

            StdDraw.picture(0,0,"./images/starfield.jpg");
            for (Planet p : plist) {
                p.draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
        }

        StdOut.printf("%d\n", plist.length);
        StdOut.printf("%.2e\n", radius);
        for (int i = 0; i < plist.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    plist[i].xxPos, plist[i].yyPos, plist[i].xxVel,
                    plist[i].yyVel, plist[i].mass, plist[i].imgFileName);
        }

    }
}
