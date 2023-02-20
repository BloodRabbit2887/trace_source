package qichen.code.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qichen.code.entity.dto.AssemblePlankPackageDTO;
import qichen.code.exception.BusinessException;
import qichen.code.exception.ResException;
import qichen.code.model.HomeModel;
import qichen.code.model.ResponseBean;
import qichen.code.service.IWorkOrderService;

@Slf4j
@Controller
@RequestMapping("/code/home")
public class HomeController {

    @Autowired
    private IWorkOrderService workOrderService;

    @ResponseBody
    @GetMapping("/getHome")
    public ResponseBean getHome(){
        try {
            HomeModel model = workOrderService.getHomeModel();
            return new ResponseBean(model);
        }catch (BusinessException exception){
            return new ResponseBean(exception);
        }catch (Exception exception){
            exception.printStackTrace();
            log.error(exception.getMessage());
            return new ResponseBean(ResException.SYSTEM_ERR);
        }
    }

}
