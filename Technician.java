/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 **/
package ThreadSharedPrinter;

public class Technician extends Thread {

    protected LaserPrinter laserPrinter;
    //protected String name;

    /* the default constructor for the technician class**/
    public Technician(String name, ThreadGroup group, LaserPrinter laserPrinter) {
        super(group, name);
        this.laserPrinter = laserPrinter;
    }


}
