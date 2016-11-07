//
//  RepeatListVC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 7/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

protocol RepeatDataTarget {
    /*
     * func commitRepeatData([RepeatDataKey:Any])
     * - Saves Repeat Data into self.rpt_voidd
     * NOTE: Will Terminate excutable if is nil.
     * [Argument]
     * rpt_time - Time repeat will trigger
     * rpt_day - Days repeat will trigger
     * rpt_stopdate - Date/Time Repeat will be disabled
     */
    func commitRepeatData(rpt_time:Date,rpt_day:Array<Bool>,rpt_stopdate:Date)
}

class RepeatListVC: UITableViewController {
    //Links
    //UI Elements
    @IBOutlet weak var bbtn_finish: UIBarButtonItem!
    @IBOutlet weak var datepick_stopdate: UIDatePicker!
    @IBOutlet weak var datepick_repeattime: UIDatePicker!

    //Data
    var stop_date:Date!
    var repeat_time:Date!
    var repeat_day:Array<Bool>!

    //Events
    override func viewDidLoad() {
        super.viewDidLoad()

        //Init Data
        self.stop_date = Date()
        self.repeat_time = Date()
        self.repeat_day = Array<Bool>()
        for _ in 0..<7
        {
            self.repeat_day.append(false)
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        //Reset Data
        self.stop_date = Date()
        self.repeat_time = Date()
        self.repeat_day = Array<Bool>()
        for _ in 0..<7
        {
            self.repeat_day.append(false)
        }

        //Reset UI Elements
        self.datepick_stopdate.date = Date()
        self.datepick_repeattime.date = Date()
        //Reset Day Cells
        for i in 0..<7
        {
            let cell = self.tableView.cellForRow(at: IndexPath(row: i, section: 2))!
            cell.accessoryType = UITableViewCellAccessoryType.none
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    //UI Events
    @IBAction func datepick_stopdate_action(_ sender: UIDatePicker) {
        self.stop_date = sender.date
    }


    @IBAction func datepick_repeattime_action(_ sender: UIDatePicker) {
        self.repeat_time = sender.date
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
        if let sge_idf = segue.identifier
        {
            switch sge_idf
            {
            case "uisge.voidd.edit.unwind":
                let destvc = (segue.destination as! RepeatDataTarget)
                destvc.commitRepeatData(rpt_time: self.repeat_time, rpt_day: repeat_day, rpt_stopdate: self.stop_date)
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
