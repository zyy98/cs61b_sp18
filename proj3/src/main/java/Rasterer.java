import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private double rootUllat = MapServer.ROOT_ULLAT;
    private double rootUllon = MapServer.ROOT_ULLON;
    private double rootLrlat = MapServer.ROOT_LRLAT;
    private double rootLrlon = MapServer.ROOT_LRLON;
    private double rootSize = MapServer.TILE_SIZE;
    private double rootLonDPP = calcLonDPP(rootLrlon,rootUllon,rootSize);
    private double maxDepth = 7;


    public Rasterer() {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);

        double ullon = params.get("ullon");
        double ullat = params.get("ullat");
        double lrlon = params.get("lrlon");
        double lrlat = params.get("lrlat");
        double height = params.get("h");
        double width = params.get("w");

        double LonDPP = calcLonDPP(lrlon, ullon, width);
        int depth = findDepth(LonDPP);
        double lonGridSize = (rootLrlon-rootUllon)/Math.pow(2,depth);
        double latGridSize = (rootUllat - rootLrlat)/Math.pow(2,depth);

        int[] res = numGrid(ullon,ullat,lrlon,lrlat,lonGridSize,latGridSize,depth);
        String[][] render_grid = generatePicGird(depth,res);

        Map<String, Object> results = new HashMap<>();
        results.put("render_grid",render_grid);
        results.put("raster_ul_lon",rootUllon + lonGridSize * res[0]);
        results.put("raster_ul_lat",rootUllat - latGridSize * res[1]);
        results.put("raster_lr_lon",rootUllon + lonGridSize * (res[2]+1));
        results.put("raster_lr_lat",rootUllat - latGridSize * (res[3]+1));
        results.put("depth", depth);
        results.put("query_success", isValid(ullon,ullat,lrlon,lrlat));

        System.out.println(results);
        //System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
                           //+ "your browser.");
        return results;
    }

    public double calcLonDPP(double lrlon, double ullon, double width) {
        return (lrlon - ullon) / width;
    }

    public int findDepth(double curLonDPP){
        double ratio = rootLonDPP/curLonDPP;
        int depth = 0;
        while(ratio > 1 && depth < maxDepth){
            ratio /= 2;
            depth += 1;
        }
        return depth;
    }

    public boolean isValid(double ullon,double ullat,double lrlon,double lrlat){
        return !(ullon > rootLrlon) && !(ullat < rootLrlat) && !(lrlon < rootUllon) && !(lrlat > rootUllat) && !(lrlon < ullon) && !(lrlat > ullat);
    }
    public int[] numGrid(double ullon,double ullat,double lrlon,double lrlat,double lonGridSize,double latGridSize,double depth){
        int[] res = new int[4];
        if (ullon < rootUllon){
            res[0] = 0;
        }else{
            res[0] = (int) Math.floor((ullon - rootUllon)/lonGridSize);
        }
        if (ullat > rootUllat){
            res[1] = 0;
        }else{
            res[1] = (int) Math.floor((rootUllat-ullat)/latGridSize);
        }
        if (lrlon > rootLrlon){
            res[2] = (int) Math.pow(2,depth) - 1;
        }else{
            res[2] = (int) Math.floor((lrlon - rootUllon)/lonGridSize);
        }
        if (lrlat < rootLrlat){
            res[3] = (int) Math.pow(2,depth) - 1;
        }else{
            res[3] = (int) Math.floor((rootUllat-lrlat)/latGridSize);
        }
        return res;
    }
    public String[][] generatePicGird(int depth, int[] res){
        String[][] render_grid = new String[(res[3]-res[1]+1)][(res[2]-res[0]+1)];
        for(int i = 0;i<=(res[3]-res[1]);i++){
            for(int j = 0;j <= (res[2]-res[0]);j++){
                int y = i + res[1];
                int x = j + res[0];
                render_grid[i][j] = "d" + depth + "_x" + x + "_y" + y + ".png";
            }
        }
        return render_grid;
    }

}
