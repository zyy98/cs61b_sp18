public class Planet {
    double xxPos;
    double yyPos;
    double xxVel;
    double yyVel;
    double mass;
    String imgFileName;
    static final double G = 6.67e-11;

    public Planet(double xP, double yP, double xV,
                  double yV, double m, String img) {
        this.xxPos = xP;
        this.yyPos = yP;
        this.xxVel = xV;
        this.yyVel = yV;
        this.mass = m;
        this.imgFileName = img;
    }

    public Planet(Planet p) {
        this.xxPos = p.xxPos;
        this.yyPos = p.yyPos;
        this.xxVel = p.xxVel;
        this.yyVel = p.yyVel;
        this.mass = p.mass;
        this.imgFileName = p.imgFileName;
    }

    public double calcDistance(Planet p) {
        double dx = this.xxPos - p.xxPos;
        double dy = this.yyPos - p.yyPos;
        double dis = Math.sqrt(dx * dx + dy * dy);
        return dis;
    }

    public double calcForceExertedBy(Planet p) {
        double dis = calcDistance(p);
        double force = p.mass * this.mass * G / Math.pow(dis, 2);
        return force;
    }

    public double calcForceExertedByX(Planet p) {
        if (calcDistance(p) == 0){
            return 0;
        }
        double cos = (p.xxPos - this.xxPos) / calcDistance(p);
        return calcForceExertedBy(p) * cos;
    }

    public double calcForceExertedByY(Planet p) {
        if (calcDistance(p) == 0){
            return 0;
        }
        double sin = (p.yyPos - this.yyPos) / calcDistance(p);
        return calcForceExertedBy(p) * sin;
    }

    public double calcNetForceExertedByX(Planet[] plist) {
        double dis = 0;
        for (Planet p : plist) {
            dis += calcForceExertedByX(p);
        }
        return dis;
    }

    public double calcNetForceExertedByY(Planet[] plist) {
        double dis = 0;
        for (Planet p : plist) {
            dis += calcForceExertedByY(p);
        }
        return dis;
    }

    public void update(double dt, double fX, double fY) {
        double ax = fX/this.mass;
        double ay = fY/this.mass;
        this.xxVel += dt * ax;
        this.yyVel += dt * ay;
        this.xxPos += dt * this.xxVel;
        this.yyPos += dt * this.yyVel;
    }
    public void draw(){
        String planetpath = "./images/" + this.imgFileName;
        StdDraw.picture(this.xxPos,this.yyPos,planetpath);
    }
}