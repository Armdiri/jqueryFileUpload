	// dragFileUpload
	@RequestMapping(value="/dragFileUpload", method=RequestMethod.POST)
	//@ResponseBody
	public void fileUpload(HttpServletRequest request, HttpServletResponse response
			, @RequestParam("uploadfile") MultipartFile uploadfile
			, @RequestParam("programType") String programType
			) throws Exception{
		if (logger.isDebugEnabled()){ logger.debug("////////// Welcome POST /dragFileUpload"); }
		Map<String, Object> map = new HashMap<String, Object>();
		logger.info("file info : "+ uploadfile.getOriginalFilename().toString());
		logger.info("file info size : "+ uploadfile.getSize());
		logger.info("file type : "+ programType);
		
		String serverPath = this.getClass().getResource("/").getPath();
		serverPath = serverPath.substring(0, serverPath.length() -1);
		logger.info("file path : "+ serverPath);
		try {
			String oriFileName = uploadfile.getOriginalFilename().toString();
			String oriFileExt = oriFileName.substring(oriFileName.lastIndexOf(".")+1, oriFileName.length());
			if(uploadfile.getSize() <= 3145728){
				if(serverPath.indexOf("WEB-INF") > -1){ serverPath = serverPath.substring(0, serverPath.lastIndexOf("WEB-INF")-1); }
				System.out.println(serverPath);
				File targetDir = new File(serverPath +"/resources/download/"+ programType);
				if(!targetDir.exists()){ 
					targetDir.mkdirs();
				}
				
				String fileServerPath = serverPath +"/resources/download/"+ programType;
				String realFileName = UUID.randomUUID().toString() + "."+ oriFileExt; 
				uploadfile.transferTo(new File(fileServerPath +"/"+ realFileName));
					
				map.put(RESULT, SUCCESS);
				map.put("filename", oriFileName);
				map.put("fileUrl", "/resources/download/"+ programType +"/"+ realFileName);
			}else{
				map.put(RESULT, FAIL);
				map.put(MSG, "용량을 초과하였습니다. (최대 3M)");
			}
		} catch (Exception e) {
			map.put(RESULT, FAIL);
			map.put(MSG, e.getMessage());
		}
		Tool.jsonObjectSend(response, map);
	}

	@RequestMapping(value="/dragFileDownload", method=RequestMethod.GET)
	@ResponseBody
	public void download(HttpServletResponse response, HttpServletRequest request) throws Exception{
		if (logger.isDebugEnabled()) { logger.debug("////////// Welcome GET /download"); }
		try {
			// 파일명 가져오기
	        String fileUrl = request.getParameter("fileUrl");
	        String fileNm = request.getParameter("fileNm");
	        String fileServerPath = request.getParameter("fileServerPath")==null?"":request.getParameter("fileServerPath");
	        
			System.out.println("/fileUrl ::"+ fileUrl);
			System.out.println("/fileNm ::"+ fileNm);
			
			if(fileServerPath.equals("")){
				fileServerPath = this.getClass().getResource("/").getPath();
				fileServerPath = fileServerPath.substring(0, fileServerPath.length() -1);
			}
	        System.out.println("/fileServerPath ::"+ fileServerPath);
	        
	        // 경로 가져오기
	        File file = null;
	        if(!fileServerPath.equals("")){
	        	fileServerPath = fileServerPath.substring(0, fileServerPath.lastIndexOf("WEB-INF")-1);
	        	file = new File(fileServerPath + fileUrl);
	        }else{
	        	file = new File(serverPath + fileUrl);
	        }
	         
	        // 무조건 다운로드하도록 설정
	        response.setHeader("Content-type", "application/unknown");
	        response.setHeader("Content-Disposition","attachment;filename=\"" + new String(fileNm.getBytes("euc-kr"),"8859_1") + "\";");

	        // 요청된 파일을 읽어서 클라이언트쪽으로 저장한다.
	        FileInputStream fileInputStream = new FileInputStream(file);
	        ServletOutputStream servletOutputStream = response.getOutputStream();
	         
	        byte b [] = new byte[1024];
	        int data = 0;
	         
	        while((data=(fileInputStream.read(b, 0, b.length))) != -1){ servletOutputStream.write(b, 0, data); }
	        servletOutputStream.flush();
	        servletOutputStream.close();
	        fileInputStream.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}