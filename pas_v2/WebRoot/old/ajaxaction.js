//����ȫ�ֵ�xml����
var xmlHttp = null;
 function GetXmlHttpObject()
 {
   var xmlHttp=null;
   try
     {
     // Firefox, Opera 8.0+, Safari
     xmlHttp=new XMLHttpRequest();
     }
   catch (e)
     {
     // Internet Explorer
     try
       {
       xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
       }
     catch (e)
       {
       xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
       }
     }
  if(xmlHttp == null)
   alert("�Բ���,�����������֧��Ajax");
   return xmlHttp;
 }

function sendGetAction(url,myStateChanged){
   //get����
 
  if (xmlHttp==null)
   {
    xmlHttp=GetXmlHttpObject();

  }
  
   //ָ����Ӧ����
  xmlHttp.ONREADYSTATECHANGE=myStateChanged;
   xmlHttp.open("GET",url,true);
   xmlHttp.send(null);
 }

function getQuery(form){
 //����Ԫ��ƴ��Query����
    queryString="";
    
  
     var numberElements =  form.elements.length;
     for(var i = 0; i < numberElements; i++) {
         if(i < numberElements-1) {
             queryString += form.elements[i].name+"="+
                            encodeURI(encodeURI(form.elements[i].value))+"&";
         } else {
             queryString += form.elements[i].name+"="+
                            encodeURI(encodeURI(form.elements[i].value));
         }

    }
  return queryString;
    
 }

function sendGetForm(form,myStateChanged){
   //��GET�ύ
  var queryString = getQuery(form);
   var url = form.action;
   url = url+"?"+queryString+"&sid="+Math.random();
   sendGetAction(url,myStateChanged);
  
 }

function sendPostAction(url,strQuery,myStateChanged){
 //post����

   if (xmlHttp==null)
   {
   xmlHttp=GetXmlHttpObject();
  
   }
   xmlHttp.open("POST",url,true);
   xmlHttp.ONREADYSTATECHANGE=myStateChanged;
   xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
   xmlHttp.send(strQuery);//��������
};

function sendPostForm(form,myStateChanged){
 //��post�ύ
  var queryString = getQuery(form);
   var url = form.action;
   sendPostAction(url,queryString,myStateChanged);

}