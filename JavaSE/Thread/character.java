import java.io.*;

public class character {
	public static void main(String[] args) throws IOException {
		// 创建共享的变量
		together together = new together();
		// 创建读取线程
		Thread t1 = new Thread(new read(together));
		// 创建输出线程
		Thread t2 = new Thread(new print(together));
		t1.start();
		t2.start();
	}
}

class together { // 共同的变量
	char[] buffer = new char[10];
	boolean readable = false; // false 数组里面没有数据
	int eof = 0; // 等于-1的时候表示到文件结尾
	BufferedReader br;
	{
		try {
			br = new BufferedReader(new FileReader("/home/liyunfeng/ab.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

class read implements Runnable {
	private together to;

	public read(together to) {
		this.to = to;
	}

	@Override
	public void run() {
		// read char
		System.out.println("开始读取");
		while (true) {
			if (to.eof == -1) break;
			synchronized (to) {
				if (to.readable == true) {
					try {
						to.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// 被唤醒之后
				try {
					to.eof = to.br.read(to.buffer, 0, 10);
					to.readable = true;
					to.notify();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class print implements Runnable {
	private together to;

	public print(together to) {
		this.to = to;
	}

	@Override
	public void run() {
		// print char
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (to.eof == -1) break;
			synchronized (to) {
				if (to.readable == false) {
					try {
						to.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// 被唤醒之后
				System.out.println(to.buffer);
				to.readable = false;
				to.notify();
			}
		}
		System.out.println("读取完毕");
	}
}