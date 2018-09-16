package com.tessModule.tess.controller;

import com.tessModule.tess.common.properties.LoadProperties;

class init {
	
		private static init instance = new init();
		
		// 생성자
		private init() {
			if(getInstance()==null){
				System.load(LoadProperties.DLL_PATH);
			}
		}
		
		public static init getInstance () {
			return instance;
		}
	
//    @Resource(name="configProps")
//    private Properties config;
//    
//    public String setFromConfig(String dllPath) {
//        return dllPath;
//    }
//
//
//
//	@Value("#{configProps['imgPath']}")
//	protected String imgPath;
	
//	public init() {
//		try {
//			// Properties 설정
//			Properties props = new Properties();
//			System.out.println("dllPath : "+props.getProperty("dllPath"));
//			System.out.println("dllPath : "+props.getProperty("#{configProps['dllPath']}"));
//			System.out.println("dllPath : "+dllPath);
////			System.load(dllPath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
		
//	    static {
//	    	Properties props = new Properties();
//				try {
//					props.load(init.class.getClassLoader().getResourceAsStream("/config.properties"));
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				System.out.println(" : "+props.getProperty("dllPath"));
//				System.load(props.getProperty("dllPath"));
//	    }
	
}
