package com.sse.ws
{
	import com.modestmaps.TweenMap;
	import com.modestmaps.core.MapExtent;
	import com.modestmaps.geo.Location;
	import com.modestmaps.geo.MercatorProjection;
	import com.modestmaps.overlays.Polyline;
	import com.sse.base.*;
	import com.mmap.tip.PopMarker;
	import com.sse.util.Radix;
	
	import flash.geom.Point;
	
	import mx.controls.Alert;
	import mx.rpc.AbstractOperation;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	public class Routing extends WSBase
	{	
		private var operP:AbstractOperation;
		private var operWP:AbstractOperation;
		private var router:WSRouter;
		
		public function Routing(wsdl:String,map:TweenMap)
		{
			super(wsdl,map);
			
			operP = ws.getOperation("plan");
			operP.addEventListener(FaultEvent.FAULT,OnFault);
			operP.addEventListener(ResultEvent.RESULT,OnPResult);
			
			operWP = ws.getOperation("webPlan");
			operWP.addEventListener(FaultEvent.FAULT,OnFault);
			operWP.addEventListener(ResultEvent.RESULT,OnWPResult);
		}
		
		public function plan(arg0:WSRouter):void
		{		
			router = arg0;			
			operP.send(arg0);
		}
		
		public function webPlan(arg0:WSRouter):void
		{
			router = arg0;
			operWP.send(arg0);
		}
		
		private function OnPResult(event:ResultEvent):void
		{	
			var result:WSResult = new WSResult(event.message.body.toString());			
			if(result.resultCode==1){
				var guid:String = "总行程:"+result.json.dis+"米--用时:"+result.json.cost+"分钟 \n";
				var minx:Number = result.json.minx;
				var miny:Number = result.json.miny;
				var maxx:Number = result.json.maxx;
				var maxy:Number = result.json.maxy;
				var segs:Array = result.json.segs;
				for(var i:int=0;i<segs.length;i++){
					segs[i].name;
					segs[i].kind;
					segs[i].attrib;
					segs[i].circle;
					segs[i].light;
					segs[i].roads;
					segs[i].vertexes;
				}
				var guids:Array = result.json.guids;
				for(var j:int=0;j<guids.length;j++){					
					guid += guids[j].name+"--行使--"+guids[j].len+"米["+guids[j].cost+"秒]--"+guids[j].turn+" \n";
				}
				Alert.show(guid);
			}else{
				Alert.show("Fail:"+result.faultString);
			}
		}
		
		private function OnWPResult(event:ResultEvent):void
		{
			var result:WSResult = new WSResult(event.message.body.toString());
			if(result.resultCode==1){
				var guid:String = "总行程:"+result.json.dis+"米--用时:"+result.json.cost+"分钟 \n";				
				var minx:Number = result.json.minx;
				var miny:Number = result.json.miny;
				var maxx:Number = result.json.maxx;
				var maxy:Number = result.json.maxy;
				var cx:Number = (minx+maxx)/2;
				var cy:Number = (miny+maxy)/2;				
				
				var locArr:Array = new Array();
				var guids:Array = result.json.guids;				
				for(var i:int=0;i<guids.length;i++){					
					guid += guids[i].name+"--行使--"+guids[i].len+"米["+guids[i].cost+"秒]--"+guids[i].turn+" \n";										
					
					var varr:Array = (guids[i].vertexes as String).split(";");
					for(var j:int=0;j<varr.length;j++){
						var xy:Array = (varr[j] as String).split(",");
						var x:Number = Radix.x2h(xy[0],36)+cx;
						var y:Number = Radix.x2h(xy[1],36)+cy;
						var loc:Location = MercatorProjection.MC2LL(new Point(x,y));
						locArr.push(loc);
					}					
				}				
				var lcolor:Number = 0xff0000;
				if(router.preference=="OnFoot"){
					lcolor = 0x00ff00;
				}else if(router.preference=="Shortest"){
					lcolor = 0x0000ff;
				}
				var polyline:Polyline = new Polyline(new Date().toLocaleString(),locArr,3,lcolor);				
				this.map.polylineClip.addPolyline(polyline);
				
				this.map.putMarker(new Location(router.startPoint.y,router.startPoint.x),new PopMarker("Start"));
				this.map.putMarker(new Location(router.endPoint.y,router.endPoint.x),new PopMarker("End"));
				
				var min:Location = MercatorProjection.MC2LL(new Point(minx,miny));
				var max:Location = MercatorProjection.MC2LL(new Point(maxx,maxy));				
				this.map.setExtent(new MapExtent(min.lat,max.lat,min.lon,max.lon));
				Alert.show(guid);
			}else{
				Alert.show("Fail:"+result.faultString);
			}
		}

	}
}