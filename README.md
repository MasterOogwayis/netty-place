# netty-place
netty-demo



2018年11月1日23:15:08
1. PrintWriter println 和 write的区别 ，wirte不会加上换行符，readLine以换行符为结束标志，会导致 对面readLine收不到结束标志从而无限阻塞，没反应
2. PrintWriter 每次发消息都需要flush,才能推送出去
