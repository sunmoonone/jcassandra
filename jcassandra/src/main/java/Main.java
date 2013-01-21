import smn.learn.downloader.Downloader;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Downloader d=new Downloader();
		System.out.print(d.httpGet("http://www.tfengyun.com/data.php?type=back_history&id=2"));
	}

}
