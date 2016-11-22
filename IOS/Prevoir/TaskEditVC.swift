//
//  TaskEditVC.swift
//  Skeem
//
//  Created by Zhu Zhan Yan on 2/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

/*
 * public enum TaskType:Int
 * - Defines task type
*/
public enum TaskType:Int
{
    case oneshot = 0
    case repetitive = 1
}

class TaskEditVC: UIViewController,UITextViewDelegate,UITextFieldDelegate {
    //Properties
    //UI Elements
    @IBOutlet weak var bbtn_finish: UIBarButtonItem!
    @IBOutlet weak var segctl_type: UISegmentedControl!
    @IBOutlet weak var textfield_name: UITextField!
    @IBOutlet weak var textview_descript: UITextView!
    @IBOutlet weak var datepick_deadline: UIDatePicker!
    @IBOutlet weak var datepick_duration: UIDatePicker!
    @IBOutlet weak var switch_drsn_affinity: UISwitch!
    @IBOutlet weak var datepick_drsn_affinity: UIDatePicker!
    @IBOutlet weak var cnstrt_label_deadline: NSLayoutConstraint!
    @IBOutlet weak var cnstrt_datepick_deadline: NSLayoutConstraint!
    @IBOutlet weak var cnstrt_datepick_drsn_affinity: NSLayoutConstraint!

    //Link
    var DBC:SKMDataController!

    //Data
    var task_name:String!
    var task_subject:String!
    var task_descript:String!
    var task_deadline:Date!
    var task_duration:Int!
    var task_drsn_affinity:Int!
    var rpttask_repeat_loop:[TimeInterval]!
    var rpttask_deadline:Date?

    //Status
    var edit:Bool!
    var repeat_data:Bool!
    var task_type:TaskType!

    //Functions
    //Setup
    /*
     * public func loadAddTask()
     * - Setup View Controller for add task
    */
    public func loadAddTask()
    {
        //Update Status
        self.edit = false
        self.repeat_data = false
        self.task_type = TaskType.oneshot

        self.resetData()
        self.updateUI()
    }

    /*
     * public loadEditTask(task:SKMTask)
     * - Setup View Controller for editing SKMTask specifed by task
     * [Arguments]
     * task - The task to edit
    */
    public func loadEditTask(task:SKMTask)
    {
        //Update Status
        self.edit = true
        self.repeat_data = false
        if let rpttask = (task as? SKMRepeatTask)
        {
            //Task is repetitive
            self.task_type = TaskType.repetitive

            //Load Data
            self.task_name = task.name
            self.task_subject = task.subject
            self.task_deadline = (task.deadline as Date)
            self.task_duration = task.duration
            self.task_drsn_affinity = task.duration_affinity
            self.rpttask_repeat_loop = rpttask.repeat_loop
            self.rpttask_deadline = rpttask.repeat_deadline as Date?
        }
        else
        {
            //Task is oneshot
            self.task_type = TaskType.oneshot
            self.task_name = task.name
            self.task_subject = task.subject
            self.task_deadline = (task.deadline as Date)
            self.task_duration = task.duration
            self.task_drsn_affinity = task.duration_affinity
        }

        //Update UI
        self.updateUI()
    }
    
    //Data
    /*
     * public func resetData()
     * - Reset Data to default values
    */
    public func resetData()
    {
        self.task_name = ""
        self.task_subject = ""
        self.task_descript = ""
        self.task_deadline = Date()
        self.task_duration = 0
        self.task_drsn_affinity = 0
        self.rpttask_repeat_loop = Array<TimeInterval>()
        self.rpttask_deadline = Date() //Init to current date
    }

    /*
     * public func vaildateData() -> Bool
     * - Vaildates the current data
     * [Reuturn]
     * Bool - Returns true if data is vaild, false otherwise.
    */
    public func vaildateData() -> Bool
    {
        let date = Date() //Init to current date

        //Vaidate Data
        if self.task_name.characters.count <= 0
        {
            return false
        }

        //Subject
        //if self.task_subject.characters.count <= 0
        //{
        //   return false
        //}

        //Task Descirption: No vaildation needed.

        if self.task_deadline.compare(date) != ComparisonResult.orderedDescending && self.task_type == TaskType.oneshot
        {
            //task_deadline >= date
            return false
        }

        if self.task_duration <= 0
        {
            return false
        }

        if self.switch_drsn_affinity.isOn == true &&  self.task_drsn_affinity <= 0
        {
            return false
        }

        return true
    }

    /*
     * public func commitData()
     * - Commit data to datebase
     * [Return]
     * Bool - Returns true if database commit is succesful, false otherwise.
    */
    public func commitData() -> Bool
    {
        if self.vaildateData() == false
        {
            abort()
        }

        var rst:Bool!

        if self.task_type == TaskType.oneshot
        {

            if self.edit == false
            {
                rst = self.DBC.createOneshotTask(name: self.task_name, subject: self.task_subject, description: self.task_descript, deadline: self.task_deadline as NSDate , duration:self.task_duration , duration_affinity: self.task_drsn_affinity)
            }
            else
            {
                rst = self.DBC.updateOneshotTask(name: self.task_name, subject: self.task_subject, description: self.task_descript, deadline: self.task_deadline as NSDate , duration:self.task_duration , duration_affinity: self.task_drsn_affinity)
            }
        }
        else if self.task_type == TaskType.repetitive && self.repeat_data == true
        {
            if self.edit == false
            {
                rst = self.DBC.createRepeativeTask(name: self.task_name, subject: self.task_subject, description: self.task_descript, repeat_loop: self.rpttask_repeat_loop, duration: self.task_duration, duration_affinity: self.task_drsn_affinity, deadline: (self.rpttask_deadline as NSDate?)!)
            }
            else
            {
                rst = self.DBC.updateRepeativeTask(name: self.task_name, subject: self.task_subject, description: self.task_descript, repeat_loop: self.rpttask_repeat_loop, duration: self.task_duration, duration_affinity: self.task_drsn_affinity, deadline: self.rpttask_deadline as NSDate?)
            }
        }
        else
        {
            abort()
        }

        return rst
    }

    //UI
    /*
     * public func updateUI()
     * - Update UI to current data
    */
    public func updateUI()
    {
        if self.task_type == TaskType.oneshot
        {
            self.segctl_type.selectedSegmentIndex = TaskType.oneshot.rawValue
            self.textfield_name.text = self.task_name
            self.textview_descript.text = self.task_descript
            self.datepick_deadline.date =  self.task_deadline
            self.datepick_duration.countDownDuration = TimeInterval(self.task_duration)
            self.datepick_drsn_affinity.countDownDuration = TimeInterval(self.task_drsn_affinity)

            //Show Deadline controls
            self.cnstrt_label_deadline.constant = 21.0
            self.cnstrt_datepick_deadline.constant = 200.0

            //Finish Button Label
            self.bbtn_finish.title = "Done"
        }
        else if self.task_type == TaskType.repetitive
        {
            self.segctl_type.selectedSegmentIndex = TaskType.repetitive.rawValue
            self.textfield_name.text = self.task_name
            self.textview_descript.text = self.task_descript
            self.datepick_duration.countDownDuration = TimeInterval(self.task_duration)
            self.datepick_drsn_affinity.countDownDuration = TimeInterval(self.task_drsn_affinity)

            //Hide Deadline controls
            self.cnstrt_label_deadline.constant = 0.0
            self.cnstrt_datepick_deadline.constant = 0.0

            //Finish Bar Button Label
            if self.repeat_data == false && self.task_type == TaskType.repetitive
            {
                self.bbtn_finish.title = "Next"
            }
            else
            {
                self.bbtn_finish.title = "Done"
            }
        }
        else
        {
            abort()
        }
    }

    //Events
    override func viewDidLoad() {
        super.viewDidLoad()

        self.task_type = TaskType.oneshot
        self.edit = false
        self.repeat_data = false

        //Init Task
        let appdelegate = (UIApplication.shared.delegate as! AppDelegate)
        self.DBC = appdelegate.DBC

        //Reset UI
        self.resetData()
        self.updateUI()

        //Update UI
        self.view.addGestureRecognizer(UITapGestureRecognizer(target: self.view, action: #selector(self.view.endEditing(_:)))) //Dismiss Keyboard On Outside Tap
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()

        //Reset UI
        self.resetData()
        self.updateUI()
    }

    //UI Events
    @IBAction func sgectl_type_action(_ sender: UISegmentedControl) {
        if sender.selectedSegmentIndex == VoidDurationType.oneshot.rawValue
        {
            self.task_type = TaskType.oneshot
            self.updateUI()
        }
        else if sender.selectedSegmentIndex == VoidDurationType.repetitive.rawValue
        {
            self.task_type = TaskType.repetitive
            self.updateUI()
        }
        else
        {
            abort()
        }
    }

    @IBAction func textfield_name_action(_ sender: UITextField)
    {
        if let txt = sender.text
        {
            self.task_name = txt
        }
    }

    @IBAction func datepick_deadline_action(_ sender: UIDatePicker)
    {
        self.task_deadline = sender.date
    }

    @IBAction func datepick_duration(_ sender: UIDatePicker)
    {
        self.task_duration = Int(round(sender.countDownDuration))
    }

    @IBAction func switch_drsn_affinity_action(_ sender: UISwitch)
    {
        if sender.isOn == true
        {
            self.cnstrt_datepick_drsn_affinity.constant = 200.0
        }
        else
        {
            self.cnstrt_datepick_drsn_affinity.constant = 0.0
        }
    }

    @IBAction func datepick_drsn_affinity_action(_ sender: UIDatePicker)
    {
        self.task_drsn_affinity = Int(round(sender.countDownDuration))
    }

    
    @IBAction func bbtn_finish_action(_ sender: UIBarButtonItem)
    {
        if self.vaildateData() == true
        {

            if self.task_type == TaskType.oneshot
            {
                //Commit Data
                let rst = self.commitData()
                if rst == false
                {
                    //Warn user of database error
                    let artctl = UIAlertController(title: "Database Error", message: "Database Failed to save. \n Check if another void duration with the same name already exists.", preferredStyle: UIAlertControllerStyle.alert)
                    artctl.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler:{(artact:UIAlertAction) in
                        self.dismiss(animated: true, completion: nil)
                    }))
                    self.present(artctl, animated: true, completion: nil)
                }
                else
                {
                    //Segue to ListVC
                    self.performSegue(withIdentifier: "uisge.task.list.unwind", sender: self)
                }
            }
            else if self.task_type == TaskType.repetitive
            {
                if self.repeat_data == false
                {
                    //Segue to RepeatEditVC
                    self.performSegue(withIdentifier: "uisge.rpt.edit", sender: self)
                }
                else
                {
                    //Commit Data
                    let rst = self.commitData()
                    if rst == false
                    {
                        //Warn user of database error
                        let artctl = UIAlertController(title: "Database Error", message: "Database Failed to save. \n Check if another void duration with the same name already exists.", preferredStyle: UIAlertControllerStyle.alert)
                        artctl.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler:{(artact:UIAlertAction) in
                            self.dismiss(animated: true, completion: nil)
                        }))
                        self.present(artctl, animated: true, completion: nil)
                    }
                    else
                    {
                        //Segue to ListVC
                        self.performSegue(withIdentifier: "uisge.task.list.unwind", sender: self)
                    }
                }
            }
            else
            {
                abort()
            }
        }
        else
        {
            //Warn user of invaild data.
            let alertctl = UIAlertController(title: "Invaild Data", message: "Please correct invaild data", preferredStyle: UIAlertControllerStyle.alert)
            alertctl.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.cancel, handler: {(alertact:UIAlertAction) in
                self.dismiss(animated: true, completion: nil)}))
            self.present(alertctl, animated: true, completion: nil)
        }
    }

    //UITextViewDelegate Methods
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if text == "\n"
        {
            textView.resignFirstResponder()
            return false
        }

        return true
    }
    
    //UITextFieldDelegate Methods
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }

    //Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?)
    {
        if let sge_idf = segue.identifier
        {
            switch sge_idf
            {
            case "uisge.rpt.edit":
                let rpteditvc = (segue.destination as! RepeatEditVC)

                let _ = rpteditvc.view //Force Load View
                //Push Data Into View Controller
                rpteditvc.repeat_deadline = self.rpttask_deadline
                rpteditvc.repeat_time = self.task_deadline
                rpteditvc.computeDay(rpt_lp: self.rpttask_repeat_loop)
                rpteditvc.source_vc = self
                
                rpteditvc.updateUI()
            case "uisge.task.list.unwind":
                break
            default:
                abort()
            }
        }
        else
        {
            abort()
        }
    }

    @IBAction func unwind_taskEditVC(sge:UIStoryboardSegue)
    {
        if let sge_idf = sge.identifier
        {
            switch sge_idf
            {
            case "uisge.task.edit.unwind":
                let rpteditvc = (sge.source as! RepeatEditVC)
                self.rpttask_deadline = rpteditvc.repeat_deadline
                self.task_deadline = rpteditvc.repeat_time
                self.rpttask_repeat_loop = rpteditvc.computeTimeInterval()

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
