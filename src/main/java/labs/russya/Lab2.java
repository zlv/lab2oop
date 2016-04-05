package labs.russya;

import java.net.*;
import java.io.*;
import java.util.*;
import org.apache.poi.*;
import org.apache.poi.poifs.filesystem.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

class Lab2 {

    public static void main(String[] args){

        try{
            String file = args[0];
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell;

            int rows; // No of rows
            int cols = 6;
            rows = 35;//sheet.getPhysicalNumberOfRows();
            ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
            final int coefsc = 5;
            double[][] fc = new double[coefsc][];
            for (int i=0; i<coefsc; ++i)
                fc[i] = new double[rows-2];
            int r = 1;
            for(; r < rows-1; r++) {
                ArrayList<Double> list2 = new ArrayList<Double>();
                row = sheet.getRow(r);
                if(row != null) {
                    for(int c = 1; c < cols; c++) {
                        cell = row.getCell((short)c);
                        double val = cell.getNumericCellValue();
                        fc[c-1][r-1] = val;
                        if(cell != null) {
                            list2.add(val);
                            System.out.print((new Double(val)).toString()+" ");
                        }
                    }
                    System.out.println();
                }
                list.add(list2);
            }
            double[] maxvalue = new double[cols-2];
            row = sheet.getRow(r);
            for(int c = 1; c < cols-1; c++) {
                cell = row.getCell((short)c);
                double val = cell.getNumericCellValue();
                maxvalue[c-1] = val;
            }
            LinearObjectiveFunction f = new LinearObjectiveFunction(fc[cols-2], 0);
            Collection<LinearConstraint> constraints = new
                    ArrayList<LinearConstraint>();
            for (int i = 0; i< cols-2; ++i)
                constraints.add(new LinearConstraint(fc[i],
                        Relationship.GEQ,  maxvalue[i]));

            SimplexSolver solver = new SimplexSolver();
            PointValuePair solution = solver.optimize(new MaxIter(1000), f, new
                            LinearConstraintSet(constraints),
                    GoalType.MINIMIZE, new
                            NonNegativeConstraint(true));
            double fsol = solution.getValue();
            System.out.println(fsol);
            double[] fx = solution.getPoint();
            r = 1;
            for (double x : fx) {
                row = sheet.getRow(r);
                cell = row.getCell(0);
                String name = cell.getStringCellValue();
                System.out.print(name + ": " + (new Double(x)).toString() + "\n");
                ++r;
            }
            System.out.println();
        }catch(IOException ioe){

            System.out.println(ioe);
        }
    }
}
//761575.1548886737
