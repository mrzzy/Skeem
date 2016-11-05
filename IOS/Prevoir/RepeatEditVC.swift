//
//  RepeatEditVC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 4/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

public enum RepeatDataKey
{
    case stop_date //Date - date/time repeat will terminate
    case repeat_time //Date - time to repeat each day
    case repeat_day //Array<Bool> - days to repeat of a weak
}

class RepeatEditVC: UIViewController {

    //Data
    var rpt_data:[RepeatDataKey:Any]!

    //Functions
    public func loadAddRepeatData()
    {

    }

    public func loadEditRepeatData(rptdat:[RepeatDataKey:Any])
    {
        self.rpt_data = rptdat

        //Load Repeat Data in UI Elements
    }

    
    //Events
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    override func viewWillDisappear(_ animated: Bool) {
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
