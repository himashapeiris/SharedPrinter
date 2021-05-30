package ThreadSharedPrinter;

public class LaserPrinter implements ServicePrinter {

    private String printer_id;
    private int paper_level;
    private int toner_level;
    private int document_printed;
    private ThreadGroup students;

    private Object lock = new Object();

    /*initialize the printer resources**/
    public LaserPrinter(String printer_id, ThreadGroup students) {
        this.printer_id = printer_id;
        this.paper_level = ServicePrinter.Full_Paper_Tray;
        this.toner_level = ServicePrinter.Full_Toner_Level;
        this.document_printed = 0;
        this.students = students;
    }

    @Override
    public String toString() {
        return "LaserPrinter{" +
                "printer_id=" + printer_id +
                ", paper_level=" + paper_level +
                ", toner_level=" + toner_level +
                ", document_printed=" + document_printed +
                '}';
    }

    @Override
    public void printDocument(Document document) {
        synchronized (lock) {
            System.out.println(this.toString());

            while (this.paper_level < document.getNumberOfPages() || this.toner_level < document.getNumberOfPages()) {
                try {
                    System.out.println("waiting for print documents,Currently printer doesn't fulfil the printing requirements ");
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                /*Check printing requirements for the printer**/
                if (this.paper_level > document.getNumberOfPages() && this.toner_level > document.getNumberOfPages()) {

                    /*reduce the random generated paper**/
                    paper_level = paper_level - document.getNumberOfPages();

                    /*One toner unit was used to print each page**/
                    toner_level = toner_level - document.getNumberOfPages();

                    /*increase the number of documents print**/
                    document_printed = document_printed + 1;
                    System.out.println("Document print Successfully");
                }
                /*printed details**/
                System.out.println(this.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.notifyAll();
            }
        }

    }

    @Override
    public void replaceTonerCartridge() {
        synchronized (lock) {

            /*wait until printer has sufficient level of toner to print documents**/
            while (this.toner_level > LaserPrinter.Minimum_Toner_Level) {
                try {
                    /*check available number of threads in student thread group**/
                    if (this.checkStudentAvailability()) {
                        System.out.println("student active count:" + students.activeCount());
                        System.out.println("Waiting to replace toner, printer has sufficient toner level to print");
                        lock.wait(5000);
                    } else {
                        System.out.println("Printing process has been finished");
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                /*replace toner Cartridge if current toner level less than 10**/
                if (this.toner_level < LaserPrinter.Minimum_Toner_Level) {
                    this.toner_level = LaserPrinter.Full_Toner_Level;
                    System.out.println("Toner successfully replaced");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.notifyAll();
            }
        }

    }

    @Override
    public void refillPaper() {
        synchronized (lock) {
            /*wait until printer has sufficient number of papers to print documents**/
            while (this.paper_level + LaserPrinter.SheetsPerPack > LaserPrinter.Full_Paper_Tray) {
                try {
                    /*check available number of threads in student thread group**/
                    if (this.checkStudentAvailability()) {
                        System.out.println("student active count:" + students.activeCount());

                        System.out.println("Waiting for additional pappers, printer has sufficient pappers to print");

                        lock.wait(5000);
                    } else {
                        System.out.println("Printing process has been finished ");
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                /*refill papers if the paper count less than 250**/
                if (this.paper_level + LaserPrinter.SheetsPerPack < LaserPrinter.Full_Paper_Tray) {
                    paper_level = paper_level + LaserPrinter.SheetsPerPack;
                    System.out.println("Papers successfully refilled");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.notifyAll();
            }
        }
    }

    /* method check the available live count threads in student thread group**/
    private boolean checkStudentAvailability() {
        return students.activeCount() > 0;
    }
}
