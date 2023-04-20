package com.hereo.project.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hereo.project.service.CommuService;
import com.hereo.project.service.MembersService;
import com.hereo.project.utils.MessageUtils;
import com.hereo.project.vo.BoardCategoryVO;
import com.hereo.project.vo.BoardTypeVO;
import com.hereo.project.vo.BoardVO;
import com.hereo.project.vo.MembersVO;


@Controller
public class CommuController {
	@Autowired
	CommuService boardService;
	
	@Autowired
	MembersService membersService;
	
	@GetMapping(value = {"/community/free", "/community"})
	public String home02(Model model) {
		
		ArrayList<BoardTypeVO> bt_list = boardService.getBoardType();
		BoardTypeVO bt=bt_list.get(0);
		int bt_num = bt.getBt_num();
		ArrayList<BoardVO> free_list= boardService.getBoard(bt_num);
		
		
		model.addAttribute("bt_num",bt_num);
		model.addAttribute("free_board", free_list);
		
		return "/community/free-board";
	}	
	
	@GetMapping(value = "/community/eventAcid")
	public String eventAcid(Model model) {
		ArrayList<BoardTypeVO> bt_list = boardService.getBoardType();
		BoardTypeVO bt=bt_list.get(1);
		int bt_num = bt.getBt_num();
		
		ArrayList<BoardVO> acid_list= boardService.getBoard(bt_num);
		model.addAttribute("bt_num",bt_num);
		model.addAttribute("acid_board", acid_list);
		return "/community/eventAcid-board";
	}	
	
	@GetMapping(value = "/community/findHero")
	public String findHero(Model model) {
		ArrayList<BoardTypeVO> bt_list = boardService.getBoardType();
		BoardTypeVO bt=bt_list.get(2);
		int bt_num = bt.getBt_num();
		
		ArrayList<BoardVO> hero_list= boardService.getBoard(bt_num);
		model.addAttribute("bt_num",bt_num);
		model.addAttribute("hero_board",hero_list);
		return "/community/findHero-board";
	}
	
	@GetMapping(value = "/community/market")
	public String market(Model model) {
		ArrayList<BoardTypeVO> bt_list = boardService.getBoardType();
		BoardTypeVO bt=bt_list.get(3);
		int bt_num = bt.getBt_num();
		
		ArrayList<BoardVO> market_list= boardService.getBoard(bt_num);
		model.addAttribute("bt_num",bt_num);
		model.addAttribute("market_board", market_list);
		return "/community/market-board";
	}

	@GetMapping(value = "/community/writing/{bt_num}")
	public String writing(
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model,
			@PathVariable("bt_num")int bt_num
			) {
		String referer = request.getHeader("Referer");
		MembersVO user=(MembersVO)request.getSession().getAttribute("loginUser");
		if (user==null) {
			MessageUtils.alertAndGoPrepage(response, "로그인 기능이 필요한 서비스입니다.", referer);
		};
		if (user.getMe_siteauth()<1) {
			MessageUtils.alertAndGoPrepage(response, "이메일 인증이 필요한 서비스입니다.", referer);
		}
		//보드카테고리01은 지역만 가져옴, 보드카테고리02는 지역빼고 가져옴.
		ArrayList<BoardCategoryVO> boardCategory01 = boardService.getBoardCategory01(bt_num);
		ArrayList<BoardCategoryVO> boardCategory02 = boardService.getBoardCategory02(bt_num);
		
		BoardTypeVO bt = boardService.getBoardTypebyBtNum(bt_num);
		model.addAttribute("bt",bt);
		
		model.addAttribute("boardCategory01", boardCategory01);
		model.addAttribute("boardCategory02", boardCategory02);
		return "/community/commu-writingboard";
		
	}
	@PostMapping(value="/community/{bt_namebyEnglish}")
	public String enrollCommuBoard(@PathVariable("bt_namebyEnglish") String englishName,
			HttpSession session, BoardVO board, Model model) {
		int boardType;
		
		if (englishName.equals("free")){
			boardType = 1;
		} else if(englishName.equals("eventAcid")) {
			boardType =2;
		} else if(englishName.equals("findHero")) {
			boardType =3;
		} else if(englishName.equals("market")) {
			boardType = 4;
		}else {
			return null;
		}
		MembersVO user=(MembersVO)session.getAttribute("loginUser");
		boardService.enrollBoard(board,user,boardType);

		return "redirect:/community/" + englishName;
	}
	@GetMapping(value="/community/content/{bo_num}")
	public String readingBoardDetail(@PathVariable("bo_num") int bo_num, Model model,
			HttpSession session) {
		MembersVO user=(MembersVO)session.getAttribute("loginUser");
		BoardVO boardDetail =boardService.getBoardDetail(bo_num);
		int bt_num=boardDetail.getBo_bt_num();
		BoardTypeVO bt = boardService.getBoardTypebyBtNum(bt_num);
		model.addAttribute("user", user);
		model.addAttribute("bt", bt);
		model.addAttribute("detail", boardDetail);
		return "/community/board-detail";
	}
	@GetMapping(value="/community/correct/{bo_num}")
	public String correctBoard(@PathVariable("bo_num")int bo_num, Model model
			) {
		BoardVO board=boardService.getBoardDetail(bo_num);
		ArrayList<BoardCategoryVO> selectBoardCategory01 = boardService.getBoardCategory01(board.getBo_bt_num());
		ArrayList<BoardCategoryVO> selectBoardCategory02 = boardService.getBoardCategory02(board.getBo_bt_num());
		model.addAttribute("board", board);
		model.addAttribute("boardCategory01", selectBoardCategory01);
		model.addAttribute("boardCategory02", selectBoardCategory02);
		
		return "/community/commu-writingboard2";
		
	}

}
	
