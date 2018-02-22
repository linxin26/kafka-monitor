package co.solinx.kafka.monitor.utils;

public interface IClientListener {
	 /**
	  * 连接事件
	  */
	 public void onConnect();
	 /**
	  * 断开事件
	  */
	 public void onDisConnect();
}
