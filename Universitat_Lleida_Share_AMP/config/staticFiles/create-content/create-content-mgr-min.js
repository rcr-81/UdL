(function(){var d=YAHOO.util.Dom;var c=Alfresco.util.siteURL;Alfresco.CreateContentMgr=function h(j){Alfresco.CreateContentMgr.superclass.constructor.call(this,"Alfresco.CreateContentMgr",j);YAHOO.Bubbling.on("formContentReady",this.onFormContentReady,this);YAHOO.Bubbling.on("beforeFormRuntimeInit",this.onBeforeFormRuntimeInit,this);return this};YAHOO.extend(Alfresco.CreateContentMgr,Alfresco.component.Base,{options:{nodeRef:null,nodeType:"document",siteId:null},onFormContentReady:function a(l,j){var k=j[1].buttons.submit;k.set("label",this.msg("button.create"));var m=j[1].buttons.cancel;m.addListener("click",this.onCancelButtonClick,null,this)},onBeforeFormRuntimeInit:function i(k,j){j[1].runtime.setAJAXSubmit(true,{successCallback:{fn:this.onCreateContentSuccess,scope:this},failureCallback:{fn:this.onCreateContentFailure,scope:this}})},onCreateContentSuccess:function e(j){var k=null;if(j.json&&j.json.persistedObject){k=new Alfresco.util.NodeRef(j.json.persistedObject);Alfresco.Share.postActivity(this.options.siteId,"org.alfresco.documentlibrary.file-created","{cm:name}","document-details?nodeRef="+k.toString(),{appTool:"documentlibrary",nodeRef:k.toString()})}this._navigateForward(k)},onCreateContentFailure:function g(j){var k=this.msg("create-content-mgr.create.failed");if(j.json.message){k=k+": "+j.json.message}Alfresco.util.PopupManager.displayPrompt({title:this.msg("message.failure"),text:k})},onCancelButtonClick:function f(k,j){this._navigateForward()},_navigateForward:function b(j){if(YAHOO.lang.isObject(j)){history.go(-1)}else{if(document.referrer.match(/documentlibrary([?]|$)/)||document.referrer.match(/repository([?]|$)/)){history.go(-1)}else{if(this.options.siteId&&this.options.siteId!==""){window.location.href=c("documentlibrary")}else{if(Alfresco.constants.PORTLET){window.location.href=c("repository")}else{window.location.href=Alfresco.constants.URL_CONTEXT}}}}}})})();