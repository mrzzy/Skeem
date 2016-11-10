//
//  VoidDurationEditVC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

enum VoidDurationType:Int
{
    case oneshot = 0
    case repetitive = 1
}

class VoidDurationEditVC: UIViewController,UITextFieldDelegate{
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
    var rptvoidd_deadline:Date?

    //Status
    var edit:Bool!
    var repeat_data:Bool!
    var voidd_type:VoidDurationType!

    //Functions
    /*
     * public func updateUI()
     * - Updates UI Elements to current data
    */
    public func updateUI()
    {
        if self.voidd_type == VoidDurationType.oneshot
        {
            self.bbtn_finish.title = "Done"
            self.segctl_type.selectedSegmentIndex = 0
            self.textfield_name.text = self.voidd_name
            self.switch_optional.isOn = (self.voidd_asserted == true) ? false : true
            self.datepick_duration.countDownDuration = TimeInterval(self.voidd_duration)
            self.datepick_begin.date = self.voidd_begin
            self.cnstrt_label_begin.constant = 21.0
            self.cnstrt_datepicker_begin.constant = 200.0
        }
        else if self.voidd_type == VoidDurationType.repetitive
        {
            if self.repeat_data == false
            {
                self.bbtn_finish.title = "Next"
                self.segctl_type.selectedSegmentIndex = 1
                self.textfield_name.text = self.voidd_name
                self.switch_optional.isOn = (self.voidd_asserted == true) ? false : true
                self.datepick_duration.countDownDuration = TimeInterval(self.voidd_duration)
                self.datepick_begin.date = self.voidd_begin
                self.cnstrt_label_begin.constant = 0.0
                self.cnstrt_datepicker_begin.constant = 0.0
            }
            else
            {
                self.bbtn_finish.title = "Done"
                self.segctl_type.selectedSegmentIndex = 1
                self.textfield_name.text = self.voidd_name
                self.switch_optional.isOn = (self.voidd_asserted == true) ? false : true
                self.datepick_duration.countDownDuration = TimeInterval(self.voidd_duration)
                self.datepick_begin.date = self.voidd_begin
                self.cnstrt_label_begin.constant = 0.0
                self.cnstrt_datepicker_begin.constant = 0.0
            }
        }
    }

    /*
     * public func resetData()
     * - Reset View Controller Data to default values
    */
    public func resetData()
    {
        self.voidd_name = ""
        self.voidd_duration = 1
        self.voidd_begin = Date() //Init To Current Time
        self.voidd_asserted = true
        self.rptvoidd_repeat_loop = Array<TimeInterval>()
        self.rptvoidd_deadline = Date()
    }

    /*
     * public func loadAddVoidDuration()
     * - Prepare View for adding Void Duration
    */
    public func loadAddVoidDuration()
    {
        //New Void Duration
        self.voidd_type = VoidDurationType.oneshot //Default Type
        self.edit = false
        self.repeat_data = false

        self.resetData()
        self.updateUI()
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
        self.repeat_data = false
        
        //Load Void Duration Data
        if let rpt_voidd = (voidd as? PVRRepeatVoidDuration)
        {
            self.voidd_type = VoidDurationType.repetitive
            rpt_voidd.update(date: NSDate()) //Update to current date
            self.voidd_name = voidd.name
            self.voidd_begin = voidd.begin as Date
            self.voidd_asserted = voidd.asserted
            self.voidd_duration =  rpt_voidd.repeat_duration
            self.rptvoidd_deadline = rpt_voidd.repeat_deadline as Date?
            self.rptvoidd_repeat_loop = rpt_voidd.repeat_loop
        }
        else
        {
            self.voidd_type = VoidDurationType.oneshot
            self.voidd_name = voidd.name
            self.voidd_begin = voidd.begin as Date
            self.voidd_duration = voidd.duration + 1 //Void Duration Readjustment
            self.voidd_asserted = voidd.asserted
        }


        //Update UI
        self.updateUI()
    }

    /*
     * public func vaildateData() -> Bool
     * - Vaildate Data that user inputed
     * NOTE: Does not vaildate repeat data provided by RepeatEditVC
     * [Return]
     * Bool - Returns true if user entered data is vaild, false otherwise
    */
    public func vaildateData() -> Bool
    {
        //Tests
        if self.voidd_name.characters.count < 1
        {
            return false
        }

        if self.voidd_duration <= 0
        {
            return false
        }

        //Void Duration Oneshot Only
        if self.voidd_type == VoidDurationType.oneshot
        {
            let crt_date = Date() //Init to current date
            if self.voidd_begin.compare(crt_date) != ComparisonResult.orderedDescending
            {
                //voidd_begin <= current date
                return false
            }
        }

        return true
    }

    /*
     * public func commitVoidDuration() -> Bool
     * NOTE: Will Terminate Executable if data provided by user is not vaild
     * - Commits edits to Void Duration object to database or warns user of database Error.
     */
    public func commitVoidDuration()
    {
        //Data Check
        if self.vaildateData() == false
        {
            abort() //Terminates Executable
        }

        var rst:Bool!

        //Create Void Duration
        if self.edit == false
        {
            if self.voidd_type == VoidDurationType.oneshot
            {
                rst = self.DBC.createVoidDuration(name: self.voidd_name, begin: self.voidd_begin as NSDate, duration: self.voidd_duration, asserted: self.voidd_asserted)
            }
            else if self.voidd_type == VoidDurationType.repetitive && repeat_data == true
            {
                rst = self.DBC.createRepeatVoidDuration(name: self.voidd_name, begin: self.voidd_begin as NSDate, duration: self.voidd_duration, repeat_loop: self.rptvoidd_repeat_loop, repeat_deadline: self.rptvoidd_deadline as NSDate?, asserted: self.voidd_asserted)
            }
            else
            {
                abort()
            }
        }
        else
        {
            if self.voidd_type == VoidDurationType.oneshot
            {
                rst = self.DBC.updateVoidDuration(name: self.voidd_name, begin: self.voidd_begin as NSDate, duration: self.voidd_duration, asserted: self.voidd_asserted)
            }
            else if self.voidd_type == VoidDurationType.repetitive && repeat_data == true
            {
                rst = self.DBC.updateRepeatVoidDuration(name: self.voidd_name, begin: self.voidd_begin as NSDate, duration: self.voidd_duration,repeat_loop: self.rptvoidd_repeat_loop, repeat_deadline: self.rptvoidd_deadline as NSDate?, asserted: self.voidd_asserted)
            }
            else
            {
                abort()
            }
        }

        //Failed To Create New Void Duration
        if rst == false
        {
            //Warn user of database error
            let artctl = UIAlertController(title: "Database Error", message: "Database Failed to save. \n Check if another void duration with the same name already exists.", preferredStyle: UIAlertControllerStyle.alert)
            artctl.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler:{(artact:UIAlertAction) in
                self.dismiss(animated: true, completion: nil)
            }))
            self.present(artctl, animated: true, completion: nil)
        }

    }
    

    //Event Functions
    override func viewDidLoad() {
        super.viewDidLoad()

        //Load Links
        let appdelegate = (UIApplication.shared.delegate as! AppDelegate)
        self.DBC = appdelegate.DBC
        self.textfield_name.delegate = (self as UITextFieldDelegate)

        //Init Data
        self.resetData()

        //Init UI
        self.updateUI()
        self.view.addGestureRecognizer(UITapGestureRecognizer(target: self.view, action: #selector(self.view.endEditing(_:)))) //Dismiss Keyboard On Outside Tap
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    //UI Event Functions
    @IBAction func sgectl_type_action(_ sender: UISegmentedControl)
    {
        switch sender.selectedSegmentIndex
        {
        case VoidDurationType.oneshot.rawValue:
            self.voidd_type = VoidDurationType.oneshot

            //Update Data Elenents
            self.updateUI()
        case VoidDurationType.repetitive.rawValue:
            self.voidd_type = VoidDurationType.repetitive

            //Update UI Elements
            self.updateUI()
        default:
            abort() //Teminates Executable, Unknown Void Duration Type
        }

    }

    @IBAction func textfield_name_editend(_ sender: UITextField)
    {
        self.voidd_name = self.textfield_name.text!
    }

    @IBAction func switch_optional_action(_ sender: UISwitch)
    {
        self.voidd_asserted = (sender.isOn == true) ? false : true
    }

    @IBAction func datepicker_duration_action(_ sender: UIDatePicker)
    {
        self.voidd_duration = Int(sender.countDownDuration)
    }

    @IBAction func datepicker_begin_action(_ sender: UIDatePicker)
    {
        self.voidd_begin = sender.date
    }

    @IBAction func bbtn_finish_action(_ sender: UIBarButtonItem)
    {
        if self.voidd_type == VoidDurationType.oneshot
        {
            //Data Check
            if self.vaildateData() == false
            {
                //Present Invaild Data Warning to User
                let alertctl = UIAlertController(title: "Invaild Data", message: "Please correct invaild data", preferredStyle: UIAlertControllerStyle.alert)
                alertctl.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.cancel, handler: {(alertact:UIAlertAction) in
                    self.dismiss(animated: true, completion: nil)}))
                self.present(alertctl, animated: true, completion: nil)
            }
            else
            {
                self.commitVoidDuration()
                self.performSegue(withIdentifier: "uisge.voidd.list.unwind", sender: self)
            }
        }
        else if self.voidd_type == VoidDurationType.repetitive
        {
            //Data Check
            if self.repeat_data == false
            {
                if self.vaildateData() == false
                {
                    //Present Invaild Data Warning to User
                    let alertctl = UIAlertController(title: "Invaild Data", message: "Please correct invaild data", preferredStyle: UIAlertControllerStyle.alert)
                    alertctl.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.cancel, handler: {(alertact:UIAlertAction) in
                        self.dismiss(animated: true, completion: nil)}))
                    self.present(alertctl, animated: true, completion: nil)
                }
                else
                {
                    self.performSegue(withIdentifier: "uisge.rpt.edit", sender: self)
                }
            }
            else
            {
                if self.vaildateData() == false
                {
                    //Present Invaild Data Warning to User
                    let alertctl = UIAlertController(title: "Invaild Data", message: "Please correct invaild data", preferredStyle: UIAlertControllerStyle.alert)
                    alertctl.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.cancel, handler: {(alertact:UIAlertAction) in
                        self.dismiss(animated: true, completion: nil)}))
                    self.present(alertctl, animated: true, completion: nil)
                }
                else
                {
                    self.commitVoidDuration()
                    self.performSegue(withIdentifier: "uisge.voidd.list.unwind", sender: self)
                }
            }
        }
    }

    //UITextFieldDelegate Protocol
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        self.voidd_name = textField.text!
        return true
    }

    // MARK: - Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let sge_idf = segue.identifier
        {
            switch sge_idf
            {
            case "uisge.rpt.edit":
                let rpteditvc = (segue.destination as! RepeatEditVC)

                let _ = rpteditvc.view //Force Load View
                //Push Data Into View Controller
                rpteditvc.repeat_deadline = self.rptvoidd_deadline
                rpteditvc.repeat_time = self.voidd_begin
                rpteditvc.computeDay(rpt_lp: self.rptvoidd_repeat_loop)
                rpteditvc.source_vc = self

                rpteditvc.updateUI()

            case "uisge.voidd.list.unwind": break
                
            default:
                abort()
            }
        }
        else
        {
            abort()
        }
    }

    @IBAction func unwind_voidDurationEdit(sge:UIStoryboardSegue)
    {
        //Pull Dat From View Controller
        if let sge_idf = sge.identifier
        {
            switch sge_idf
            {
            case "uisge.voidd.edit.unwind":
                let rpteditvc = (sge.source as! RepeatEditVC)
                self.rptvoidd_deadline = rpteditvc.repeat_deadline
                self.voidd_begin = rpteditvc.repeat_time
                self.rptvoidd_repeat_loop = rpteditvc.computeTimeInterval()

                //Update UI
                self.repeat_data = true
                self.updateUI()
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
