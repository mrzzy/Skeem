//
//  RepeatEditVC.swift
//  Skeem
//
//  Created by Zhu Zhan Yan on 7/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class RepeatEditVC: UITableViewController {
    //Links
    //UI Elements
    @IBOutlet weak var bbtn_finish: UIBarButtonItem!
    @IBOutlet weak var datepick_stopdate: UIDatePicker!
    @IBOutlet weak var datepick_repeattime: UIDatePicker!

    //Data
    var repeat_deadline:Date!
    var repeat_time:Date!
    var repeat_day:Array<Bool>!

    //Status
    var source_vc:UIViewController!

    //Functions
    //Data
    /*
     * public func vaildateData() -> Bool
     * - Vaildates date entered by user
     * [Return]
     * Bool - true if data is vaild, false otherwise.
    */
    public func vaildateData() -> Bool
    {
        let date = Date() //Init to Current Date

        //Stop Data
        if self.repeat_deadline.compare(date) != ComparisonResult.orderedDescending
        {
            //stop date <= current date
            return false
        }

        //Repeat Time: No Vaildation Needed

        //Repeat Day
        if self.repeat_day.count <= 0
        {
            return false
        }

        return true
    }

    /*
     * public func computeTimeInterval()
     * - Derives Time interval from day selection (self.repeat_day)
     * [Return]
     * Array<TimeInterval> - Repeat Loop for a repetitive object
    */
    public func computeTimeInterval() -> Array<TimeInterval>
    {
        var repeat_loop = Array<TimeInterval>()
        let day_tint =  24 * 60 * 60
        var day_cnt = 1

        for d in self.repeat_day
        {
            if d == true
            {
                repeat_loop.append(TimeInterval(day_cnt * day_tint))
                day_cnt = 1
            }
            else
            {
                day_cnt += 1
            }
        }

        return repeat_loop
    }

    /*
     * public func computeDay(rpt_lp:Array<TimeInterva>)
     * - Derives Days from Tome interval in repeat loop
     * - Stores result in self.repeat_loop
    */
    public func computeDay(rpt_lp:Array<TimeInterval>)
    {
        var rpt_day = Array(repeating: false, count: 7)
        let day_tint = 24 * 60 * 60
        var tint_left = 0
        var day_idx = 0
        
        for tint in rpt_lp
        {
            tint_left = Int(round(tint))
            while (tint_left - day_tint) > 0
            {
                tint_left -= day_tint
                day_idx += 1
            }
            rpt_day[day_idx] = true
            day_idx += 1
        }

        self.repeat_day = rpt_day
    }
    

    /*
     * public func updateUI()
     * - Updates UI Elements to current data
    */
    public func updateUI()
    {
        //Update UI
        self.datepick_stopdate.date = self.repeat_deadline
        self.datepick_repeattime.date  = self.repeat_time
        for i in 0..<7
        {
            let cell = super.tableView(self.tableView, cellForRowAt: IndexPath(row: i, section: 2))
            if repeat_day[i] == true
            {
                cell.accessoryType = UITableViewCellAccessoryType.checkmark
            }
            else
            {
                cell.accessoryType = UITableViewCellAccessoryType.none
            }
        }
    }

    /*
     * public func resetData()
     * - Resets Data to default values
    */
    public func resetData()
    {
        //Reset Data
        self.repeat_deadline = Date()
        self.repeat_time = Date()
        self.repeat_day = Array(repeating: false, count: 7 )
    }

    //Events
    override func viewDidLoad() {
        super.viewDidLoad()

        //Init Data
        self.resetData()
        //Reset UI
        self.updateUI()
    }

    override func viewWillDisappear(_ animated: Bool) {
        //Reset Data
        self.resetData()
        //Reset UI Elements
        self.updateUI()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    //UI Events
    @IBAction func datepick_stopdate_action(_ sender: UIDatePicker) {
        self.repeat_deadline = sender.date
    }


    @IBAction func datepick_repeattime_action(_ sender: UIDatePicker) {
        self.repeat_time = sender.date
    }
    
    @IBAction func bbtn_finish_action(_ sender: UIBarButtonItem) {
        if self.vaildateData() == false
        {
            //Warn User of Invaild Data
            let artctl = UIAlertController(title: "Invaild Data", message: "Please correct invaild data", preferredStyle: UIAlertControllerStyle.alert)
            artctl.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: {(artact:UIAlertAction) in
                self.dismiss(animated: true, completion: nil)
            }))
            self.present(artctl, animated: true, completion: nil)
        }
        else
        {
            if (self.source_vc as? VoidDurationEditVC) != nil
            {
                self.performSegue(withIdentifier: "uisge.voidd.edit.unwind", sender: self)
            }
            else if (self.source_vc as? TaskEditVC) != nil
            {
                self.performSegue(withIdentifier: "uisge.task.edit.unwind", sender: self)
            }
        }
    }

    //UITableViewController Methods
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        //Select Days
        if indexPath.section == 2
        {
            //Update Data
            self.repeat_day[indexPath.row] = (self.repeat_day[indexPath.row] == true) ? false : true //Toogle Setting

            //Update UI
            if self.repeat_day[indexPath.row] == true
            {
                let cell = self.tableView.cellForRow(at: indexPath)!
                cell.accessoryType = UITableViewCellAccessoryType.checkmark
            }
            else
            {
                let cell = self.tableView.cellForRow(at: indexPath)!
                cell.accessoryType = UITableViewCellAccessoryType.none
            }
        }
    }

    //Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    }

}
