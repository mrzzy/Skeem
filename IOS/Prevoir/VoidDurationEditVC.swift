//
//  VoidDurationEditVC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class VoidDurationEditVC: UIViewController {
    //UI Elements
    @IBOutlet weak var bbtn_finish: UIBarButtonItem!
    @IBOutlet weak var segctl_type: UISegmentedControl!
    @IBOutlet weak var textfield_name: UITextField!
    @IBOutlet weak var switch_optional: UISwitch!
    @IBOutlet weak var datepick_duration: UIDatePicker!
    @IBOutlet weak var datepick_begin: UIDatePicker!
    //UI Constraints
    @IBOutlet weak var cnstrt_label_begin: NSLayoutConstraint!
    @IBOutlet weak var cnstrt_datepicker_begin: NSLayoutConstraint!

    //Link
    var DBC:PVRDataController!

    //Data
    //var voidd:PVRVoidDuration!
    //var rpt_voidd:PVRRepeatVoidDuration!
    var voidd_name:String!
    var voidd_duration:Int!
    var voidd_begin:Date!
    var voidd_asserted:Bool!
    var rptvoidd_repeat_loop:Array<TimeInterval>!
    var rptvoidd_deadline:NSDate?

    //Status
    var edit:Bool!

    //Functions
    /*
     * public func loadAddVoidDuration()
     * - Prepare View for adding Void Duration
    */
    public func loadAddVoidDuration()
    {
        //New Void Duration
        self.edit = false

        //Reset UI Elements
        self.segctl_type.selectedSegmentIndex = 0
        self.textfield_name.text = ""
        self.switch_optional.setOn(false, animated: false)
        self.datepick_duration.countDownDuration = 0
        self.datepick_begin.date = Date() //Display Current Date
    }

    /*
     * public func loadEditVoidDuration(voidd:PVRVoidDuration)
     * - Prepare View for editing Void Duration specfied by argument voidd
     * [Argument]
     * voidd - The Void Duration to edit
    */
    public func loadEditVoidDuration(voidd:PVRVoidDuration)
    {
        self.edit = true

        //Load Void Duration Data
        self.voidd_name = voidd.name
        self.voidd_begin
        if let rpt_voidd = (voidd as? PVRRepeatVoidDuration)
        {
            self.rptvoidd_deadline = rpt_voidd.repeat_deadline
            self.rptvoidd_repeat_loop = rpt_voidd.repeat_loop
        }
        

        
        //Load Void Duration Data into UI
        self.textfield_name.text = self.voidd.name
        self.switch_optional.setOn(((self.voidd.asserted == true) ? false : true), animated: false)
        self.datepick_begin.date = (self.voidd.begin as Date)

        if let rpt_voidd = (self.voidd as? PVRRepeatVoidDuration)
        {
            //Admend UI
            self.rpt_voidd = rpt_voidd
            self.segctl_type.selectedSegmentIndex = 1 //Repeat Duuration
            self.bbtn_finish.title = "Next"
            self.cnstrt_label_begin.constant = 0.0
            self.cnstrt_datepicker_begin.constant = 0.0

            //Load Data into UI
            self.datepick_duration.countDownDuration = TimeInterval(self.rpt_voidd.repeat_duration)
        }
        else
        {
            //Admend UI
            self.segctl_type.selectedSegmentIndex = 0 //Oneshot Duration

            //Load Data into UI
            self.datepick_duration.countDownDuration = TimeInterval(self.voidd.duration)
        }

    }

    /*
     * public func commitRepeatData([RepeatDataKey:Any])
     * - Saves Repeat Data into self.rpt_voidd
     * NOTE: Will Terminate excutable if self.rpt_voidd is nil.
     * [Argument]
     * rptdat - Repeat Data to commit
    */
    public func commitRepeatData(rptdat:[RepeatDataKey:Any])
    {
        if self.rpt_voidd == nil
        {
            abort() //Terminates Executable
        }

        for (key,data) in rptdat
        {
            switch key
            {
            case RepeatDataKey.stop_date:
                self.rpt_voidd.repeat_deadline = (data as! NSDate)
            case RepeatDataKey.repeat_time:
                self.rpt_voidd.begin = (data as! NSDate)
            case RepeatDataKey.repeat_day:
                let arr_day = (data as! Array<Bool>)
                let day = 24 * 60 * 60
                var day_cnt = 1
                self.rpt_voidd.repeat_loop = Array<TimeInterval>()

                for day in 0..<arr_day.count
                {
                    if arr_day[day] == true
                    {
                        self.rpt_voidd.repeat_loop.append(TimeInterval(day * day_cnt))
                        day_cnt = 1
                    }
                    else
                    {
                        day_cnt += 1
                    }
                }
            }
        }
    }

    /*
     * public func commitVoidDuration()
     * - Commits edits to Void Duration object to database
    */
    public func commitVoidDuration()
    {
        self.DBC.createvoid
    }

    //Event Functions
    override func viewDidLoad() {
        super.viewDidLoad()

        let appdelegate = (UIApplication.shared.delegate as! AppDelegate)
        self.DBC = appdelegate.DBC
    }

    override func viewWillDisappear(_ animated: Bool) {
        self.voidd = nil
        self.rpt_voidd = nil
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    //UI Event Functions
    @IBAction func sgectl_type_action(_ sender: UISegmentedControl)
    {

    }

    @IBAction func textfield_text_action(_ sender: UITextField)
    {

    }

    @IBAction func switch_optional_action(_ sender: UISwitch)
    {

    }

    @IBAction func datepicker_duration_action(_ sender: UIDatePicker)
    {

    }

    @IBAction func datepicker_begin_action(_ sender: UIDatePicker)
    {

    }

    @IBAction func bbtn_finish_action(_ sender: UIBarButtonItem)
    {
        if self.rpt_voidd != nil
        {
            //Obtain Repeat Data
            self.performSegue(withIdentifier: "uisge.rpt.edit", sender: sender)
        }
        else
        {
            //Save Data & Unwind Segue
            self.commitVoidDuration()
            self.performSegue(withIdentifier: "uisge.voidd.edit.unwind", sender: sender)
        }
    }


    @IBAction func perpareUnwind(segue:UIStoryboardSegue)
    {

    }

    // MARK: - Navigation

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let sge_idf = segue.identifier
        {
            switch sge_idf
            {
            case "uisge.rpt.edit":
                let rpteditvc = (segue.destination as! RepeatEditVC)
                    //TODO: Finish Rpt VC Add Fill in Metho
            default:
                abort()
            }
        }
        else
        {
            abort()
        }
    }
}
