package redis;

public class Ticketout {
    private static int ticketnum = 100;//所有卖票系统对应一个存票系统

    public int sellTicket() {
        if (ticketnum > 0) {
            return ticketnum--;
        }
        return -1;
    }

    public int getTicketnum() {
        return ticketnum;
    }

}
