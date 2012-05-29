/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package checktasx;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author ambal
 */
public class CheckTasx {

    /**
     * @param args the command line arguments
     */
    private Scanner scanner;
    private String[] addr;
    private int chIP, ip, ip_, pre, mask;
    private InputStream is;
    private FileOutputStream fos;
    private ArrayList<StringTokenizer> ar = new ArrayList();

    public static void main(String[] args) throws IOException, InterruptedException {
        switch (args.length) {
            case 0:
                CheckTasx chtx = new CheckTasx();
                break;
            case 1:
                CheckTasx chtx_ = new CheckTasx(args[0]);
                break;
        }
    }

    private CheckTasx() throws IOException {
        if (update()) {
            System.out.println("ipfilter создан");
        } else {
            System.err.println("Ошибка при создании ipfilter'a. Проверьте подключение к интернету.");
        }
    }

    private CheckTasx(String site) throws IOException, InterruptedException {
        System.out.println(check(site));
    }

    private int ip2int(String site) throws UnknownHostException {
        byte[] b = InetAddress.getByName(site).getAddress();
        switch (b.length) {
            case 4:
                int result = 0;
                for (int i = 0; i < 4; ++i) {
                    result += ((int) (b[i] & 0xFF) << 8 * (3 - i));
                }
                return result;
        }
        return 0;
    }

    private String int2ip(int ip) {
        StringBuilder sb = new StringBuilder(15);
        for (int shift = 24; shift > 0; shift -= 8) {
            sb.append(Long.toString((ip >>> shift) & 0xff));
            sb.append('.');
        }
        sb.append(Long.toString(ip & 0xff));
        return sb.toString();
    }

    private int mask() throws UnknownHostException {
        mask = 0xffffffff << (32 - pre());
        byte[] bytes = new byte[]{
            (byte) (mask >>> 24), (byte) (mask >> 16 & 0xff), (byte) (mask >> 8 & 0xff), (byte) (mask & 0xff)};
        mask = ip2int(InetAddress.getByAddress(bytes).getHostAddress());
        return mask;
    }

    private int pre() {
        pre = Integer.parseInt(addr[1]);
        return pre;
    }

    private String ip() throws UnknownHostException {
        ip = ip2int(addr[0]);
        return addr[0];
    }

    private String ip_() throws UnknownHostException {
        ip_ = ip + (~mask());
        return int2ip(ip_);
    }

    private void init(String addr) throws UnknownHostException {
        this.addr = addr.split("/");
        ip();
        ip_();
    }

    private boolean readFile() throws java.io.IOException, InterruptedException {
        try {
            scanner = new Scanner(new FileInputStream("ipfilter"));
            while (scanner.hasNextLine()) {
                ar.add(new StringTokenizer(scanner.nextLine()));
            }
            return true;
        } catch (IOException ex) {
            System.err.println("ipfilter отсутствует");
            return false;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private boolean check(String site) throws IOException, InterruptedException {
        if (readFile()) {
            chIP = ip2int(site);
            for (int i = 0; i < ar.size(); i++) {
                while (ar.get(i).hasMoreElements()) {
                    init(ar.get(i).nextToken());
                    if (chIP >= ip && chIP <= ip_) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            if (update()) {
                System.out.println("ipfilter создан");
                return check(site);
            } else {
                System.err.println("Ошибка при создании ipfilter'a. Проверьте подключение к интернету.");
                return false;
            }
        }
    }

    private boolean update() throws IOException {
        try {
            is = new URL("http://tasix.sarkor.uz/full").openStream();
            fos = new FileOutputStream(new File("ipfilter"));
            int c, l = is.available();
            while (((c = is.read()) != 1) && (--l > 0)) {
                fos.write(c);
            }
            return true;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        } finally {
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }
}
