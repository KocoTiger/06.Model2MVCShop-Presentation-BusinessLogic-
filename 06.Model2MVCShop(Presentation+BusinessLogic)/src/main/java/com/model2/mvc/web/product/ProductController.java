package com.model2.mvc.web.product;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;


//==> 회원관리 Controller
@Controller
public class ProductController {
	
	///Field
	@Autowired
	@Qualifier("productServiceImpl")
	private ProductService productService;
	//setter Method 구현 않음
		
	public ProductController(){
		System.out.println(this.getClass());
	}
	
	//==> classpath:config/common.properties  ,  classpath:config/commonservice.xml 참조 할것
	//==> 아래의 두개를 주석을 풀어 의미를 확인 할것
	@Value("#{commonProperties['pageUnit']}")
	//@Value("#{commonProperties['pageUnit'] ?: 3}")
	int pageUnit;
	
	@Value("#{commonProperties['pageSize']}")
	//@Value("#{commonProperties['pageSize'] ?: 2}")
	int pageSize;
	
	
	@RequestMapping("/addProductView.do")
	public String addProductView() throws Exception {

		System.out.println("/addProductView.do");
		
		return "forward:/product/addProductView.jsp";
	}
	
	
	
	
	
///////////////////////////////////////////////
	@RequestMapping("/addProduct.do")
	public String addProduct( @ModelAttribute("product") Product product, 
			
							  @RequestParam("manuDate") String manuDate ) throws Exception {

		product.setManuDate(manuDate.replace("-", ""));
		
		//원래 sql 에 날짜 받아올때 했었던 것을 이렇게 바꿔야함
		
		System.out.println("/addProduct.do");
		//Business Logic
		productService.addProduct(product);

//////////////////////////////////////////		
		return "forward:/product/addProductView.jsp";
		
		//이게 어디로 가면 좋을까
/////////////////////////////////////////
		
		
	}
	
	@RequestMapping("/getProduct.do")
	public String getProduct( @RequestParam("prodNo") int prodNo , Model model ) throws Exception {
		
		System.out.println("/getProduct.do");
		//Business Logic
		Product product = productService.getProduct(prodNo);
		// Model 과 View 연결
		model.addAttribute("product", product);
		
		//setAttribute로 쓰고 value값이 들어간다면,
		
		
		return "forward:/product/getProduct.jsp";
	}
	
	
	
	
	
	@RequestMapping("/updateProductView.do")
	public String updateProductView( @RequestParam("prodNo") int prodNo , Model model ) throws Exception{

		System.out.println("/updateProductView.do");
		//Business Logic
		Product product = productService.getProduct(prodNo);
		// Model 과 View 연결
		model.addAttribute("product", product);
		
		return "forward:/product/updateProductView.jsp";
	}
	

	
////Product에는 session정보를 사용할 수도 있고 안할 수도 있다 (선택)
// ==>결론 : product에서는 안 써도 된다 
	
	@RequestMapping("/updateProduct.do")
	public String updateProduct( @ModelAttribute("product") Product product , Model model , HttpSession session) throws Exception{

		System.out.println("/updateProduct.do");
		//Business Logic
		productService.updateProduct(product);
		
//		int sessionId=((Product)session.getAttribute("product")).getProdNo();
//		if(sessionId == (product.getProdNo())){
//			session.setAttribute("product", product);
//		}
		
		return "redirect:/getProduct.do?prodNo=" + product.getProdNo();
	}
	
//	@RequestMapping("/loginView.do")
//	public String loginView() throws Exception{
//		
//		System.out.println("/loginView.do");
//
//		return "redirect:/user/loginView.jsp";
//	}
	
	
	
//	
//	@RequestMapping("/login.do")
//	public String login(@ModelAttribute("user") Product user , HttpSession session ) throws Exception{
//		
//		System.out.println("/login.do");
//		//Business Logic
//		Product dbUser=productService.getUser(user.getProdNo());
//		
//		if( user.getPassword().equals(dbUser.getPassword())){
//			session.setAttribute("user", dbUser);
//		}
//		
//		return "redirect:/index.jsp";
//	}
	
//	@RequestMapping("/logout.do")
//	public String logout(HttpSession session ) throws Exception{
//		
//		System.out.println("/logout.do");
//		
//		session.invalidate();
//		
//		return "redirect:/index.jsp";
//	}
	
	
//	
//	@RequestMapping("/checkDuplication.do")
//	public String checkDuplication( @RequestParam("userId") String userId , Model model ) throws Exception{
//		
//		System.out.println("/checkDuplication.do");
//		//Business Logic
//		boolean result=productService.checkDuplication(userId);
//		// Model 과 View 연결
//		model.addAttribute("result", new Boolean(result));
//		model.addAttribute("userId", userId);
//
//		return "forward:/user/checkDuplication.jsp";
//	}
//	
	
	
	@RequestMapping("/listProduct.do")
	public String listProduct( @ModelAttribute("search") Search search , Model model , HttpServletRequest request) throws Exception{
		
		System.out.println("/listProduct.do");
		
		if(search.getCurrentPage() ==0 ){
			search.setCurrentPage(1);
		}
		search.setPageSize(pageSize);
		
		// Business logic 수행
		Map<String , Object> map = productService.getProductList(search);
		
		Page resultPage = new Page( search.getCurrentPage(), ((Integer)map.get("totalCount")).intValue(), pageUnit, pageSize);
		System.out.println(resultPage);
		
		// Model 과 View 연결
		model.addAttribute("list", map.get("list"));
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("search", search);
		
		return "forward:/product/listProduct.jsp";
	}
}