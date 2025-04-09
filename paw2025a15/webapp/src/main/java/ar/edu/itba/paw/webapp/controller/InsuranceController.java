package ar.edu.itba.paw.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import ar.edu.itba.paw.interfaces.services.InsuranceService;

@Controller
public class InsuranceController {

    private final InsuranceService is;

    @Autowired
    public InsuranceController(final InsuranceService is){
        this.is = is;
    }
/*TODO
    @RequestMapping("/insurances/{id:\\d+}")
    public ModelAndView profile(@PathVariable("id") long id) {
        final ModelAndView mav = new ModelAndView("insurance");
        is.getInsuranceById(id).ifPresent(insurance -> mav.addObject("insurance", insurance));
        return mav;
    }

    @RequestMapping(value = "/supersecret/insurance/create", method=RequestMethod.POST)
    public ModelAndView createInsurance(@RequestParam(value = "name", required = true) final String name, @RequestParam(value = "picId", required = true) final long picId) {
        is.create(name, picId);
        final ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping(value = "/supersecret/insurance/create", method=RequestMethod.GET)
    public ModelAndView createInsuranceForm() {
        final ModelAndView mav = new ModelAndView("createInsurance");
        return mav;
    }

    @RequestMapping(value = "/supersecret/insurance/edit/{id:\\d+}", method=RequestMethod.POST)
    public ModelAndView editInsurance(@PathVariable("id") long id) {
        //is.edit(id, name, picId); TODO
        final ModelAndView mav = new ModelAndView("index");
        return mav;
    }

    @RequestMapping(value = "/supersecret/insurance/edit/{id:\\d+}", method=RequestMethod.GET)
    public ModelAndView editInsuranceForm() {
        //TODO
        final ModelAndView mav = new ModelAndView("editInsurance");
        return mav;
    }
*/
}
