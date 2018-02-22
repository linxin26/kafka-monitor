　　Kafka Monitor为Kafka的可视化管理与监控工具，为Kafka的稳定运维提供高效、可靠、稳定的保障，这里主要简单介绍Kafka Monitor的相关功能与页面的介绍；  
Kafka Monitor主要功能：Kafka基本信息仪表盘、broker列表、topic列表、当前消费者列表、Topic添加删除、Topic数据查询；  
## 一、仪表盘
　　仪表盘分三部分：状态、图表、报警；状态栏显示了Kafka当前集群状态、Topic总数、节点数、Partition总数，并通过图表显示当前Kafka的可用性信息；有可用性图表、延迟统计图表、异常统计图表；报警栏位当前kafka的异常信息；    
## 二、broker页面
　　broker页面显示当前broker的可用总数、broker列表中显示当前Kafka集群中每个broker的基本信息、还可通过点击broker ID进入broker详情页面  
## 三、Topic页面
　　页面显示当前kafka所有topic总数、Partition总数、topic首选副本率等，并提供添加Topic功能；  
## 四、消费者页面
　　页面显示当前kafka中所有consumer列表，并显示所属group、topic等信息；  
## 五、数据查询页面
　　查询页面可通过输入topic、partition、offset与Num（消息数量）查询指定的topic中某个partition的消息；  
