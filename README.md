#使用说明
1.在d盘新建lic文件夹，将序列号.txt放入lic文件夹下

2.在jar的目录shift + 右键，打开cmd窗口执行命令；  

3.命令：  
```
mvn install:install-file -DgroupId=com.zhuozhengsoft -DartifactId=pageoffice -Dversion=5.2.0.8  -Dpackaging=jar  -Dfile=pageoffice5.2.0.8.jar
```  
maven中有多个settings文件，settings配置了不同的仓库，想指定则在上述命令后添加：  
```  
 --settings D:\InstallFile\apache-maven-3.6.3-bin\apache-maven-3.6.3\conf\getech.xml
```
4.上述命令将jar打包到本地maven仓库，刷新项目，项目就不会报jar保存在，启动项目  

5.访问localhost:8080/index，lic下会生成license.lc

