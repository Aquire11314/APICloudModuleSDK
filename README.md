# APICloudModuleSDK
便携蓝牙打印机 Apicloud 模块

在ApiCloud中使用案例 

  var uzmoduledemo = null;
	apiready = function(){
	    uzmoduledemo = api.require('moduleDemo');
	}
	//开启蓝牙
	function startBluetooth(){
          uzmoduledemo.startBluetooth();
    }
	// 代开蓝牙设置
    function toBluetoothSetting(){
          uzmoduledemo.toBluetoothSetting();
    }
	//获取蓝牙连接设备名和地址
    function getBondedDevices(){
         uzmoduledemo.getBondedDevices();
    }
   
   
    //打印
    function print(){
      var model={
        "batchNo": "123",
        "Weight": "0.00KG",
        "produceName": "55# 透析纸 60g",
        "width": "0.195000M",
        "num1": "761F467D-6628-496A-9B76-B9E8B8F44DCE",
        "location": "D28",
        "materialNo": "Y.3.001.034.000222",
        "long": "705M",
        "type": "医包行业_半成品_透析纸_55# 透析纸（台湾）_55# 透析纸 60g",
        "isFree": "被占用",
        "PurchaseOrderNo": ""
      };
      var param = {address:"00:39:75:75:38:32"};
      uzmoduledemo.connect(param, function(ret, err){
        console.log(JSON.stringify(ret));
      });
      param={pageWidth:540,pageHeight:500};
      uzmoduledemo.pageSetup(param);
      param={start_x:45,start_y:30,text:model.num1,rotate:0,ver:3,lel:0};
      uzmoduledemo.drawQrCode(param);
      param={text_x:280,text_y:30,text:"卷料编码：",fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*2,text:model.materialNo,fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*3,text:"重量：",fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*4,text:model.Weight,fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*5,text:"门幅(M)：",fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*6,text:model.width,fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*7,text:"长度(M)：",fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*8,text:model.long,fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*9,text:"批号：",fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:280,text_y:30*10,text:model.batchNo,fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={text_x:45,text_y:30*11,text:"卷料名称：",fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawTextBox(param);
      param={text_x:45,text_y:30*12,width:540,height:70,text:model.produceName,fontSize:3,rotate:0,bold:0,reverse:false,underline:false};
      uzmoduledemo.drawText(param);
      param={horizontal:0,skip:0};
      uzmoduledemo.print(param);
      uzmoduledemo.disconnect(param, function(ret, err){
        alert(JSON.stringify(ret));
      });

    }
