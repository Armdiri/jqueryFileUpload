<div style="width:500px;">
	<div id="fileuploader"><span class="ui-icon"></span><span class="ui-text">Add files...</span></div>
	<!-- 파일 삭제/다운로드 부분 -->
	<c:if test="${productFiles != null }">
		<c:forEach var="list" items="${productFiles}" >
			<div id="fileView_${list.productFileSeq}" class="ajax-file-upload-statusbar" style="width:500px;">
				<div class="ajax-file-upload-filename" style="cursor:pointer;float:left;" onclick="downloadFile('${list.filePath}','${list.fileName}')">
					<c:set var="ext" value="${fn:toLowerCase(fn:substringAfter(list.fileName,'.'))}" />
					<c:choose>
	    					<c:when test="${ext == 'csv' || ext =='xls' || ext =='xlsx'}"> <i class="fa fa-file-excel-o"></i> </c:when>
	    					<c:when test="${ext == 'doc' || ext =='hwp' || ext =='word'}"> <i class="fa fa-file-word-o"></i> </c:when>
	    					<c:when test="${ext == 'pdf' }"> <i class="fa fa-file-pdf-o"></i> </c:when>
	    					<c:when test="${ext == 'ppt' || ext == 'pptx' }"> <i class="fa fa-file-powerpoint-o"></i> </c:when>
	    					<c:when test="${ext == 'jpg' || ext == 'jpeg' || ext == 'png' || ext == 'gif' || ext == 'bmp' }"> <i class="fa fa-file-image-o"></i> </c:when>
						<c:otherwise><i class="fa fa-file"></i></c:otherwise>
					</c:choose>
					${list.fileName} <span style="font-size:11px;color:#BB6E8A;">(${list.regDt})</span>
				</div>
				<div style="float:right;"><i class="fa fa-trash-o fa-lg" onclick="deleteFile('${list.productFileSeq}')"></i></div>
			</div>
		</c:forEach>
	</c:if>
</div>

<script>
	$(document).ready(function(){
		$(".fileinput-button").mouseover(function(){
			$(this).addClass("hover");
		}).mouseout(function(){
			$(this).removeClass("hover");
		});
		
		//dragFile upload
		var ajaxData = new Object();
		ajaxData["programType"] = "order";
		
		$("#fileuploader").uploadFile({
			url : "/admin/dragFileUpload",
			multiple : true,
			fileName : "uploadfile",
			returnType: "json",
			formData : ajaxData,
			onSubmit:function(files){ console.log("onSubmit >> "+ files); },
			onSuccess:function(files,data,xhr){
				if(data.<spring:eval expression="@globalContext['RESULT']" /> != "<spring:eval expression="@globalContext['SUCCESS']" />"){
					alert(data.<spring:eval expression="@globalContext['MSG']" />);
				}
			},
			afterUploadAll:function(){ },
			onError: function(files,status,errMsg){
				console.log("onError >> "+ status +"/"+ errMsg);
			}
		});	
	});

	function deleteFile(keyvalue){ //dragFile delete
		if(confirm("Are you sure you want to delete. [yes/no]")){
			$.ajax({
				type:"GET",
				dataType:"json",
				url:"/admin/product/deleteFile/"+ keyvalue,
				success:function(data) {
					if (data.<spring:eval expression="@globalContext['RESULT']" /> == "<spring:eval expression="@globalContext['SUCCESS']" />") {
						$("#fileView_"+ keyvalue).remove();
					} else {
						alert(data.<spring:eval expression="@globalContext['MSG']" />);
					}
				},
				error : function(xhr, ajaxOpts, thrownErr){ alert(getErrorMessage(xhr.status,thrownErr)); }
			});
		}
	}
	//dragFile download
	function downloadFile(path, file){ location.href= "/admin/dragFileDownload?fileUrl="+ path +"&fileNm="+ file ; }
</script>