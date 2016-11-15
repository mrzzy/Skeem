//
//  ScheduleTaskTBC.swift
//  Prevoir
//
//  Created by Zhu Zhan Yan on 11/11/16.
//  Copyright © 2016 SSTInc. All rights reserved.
//

import UIKit

class ScheduleTaskTBC: UITableViewCell {
    //UI Element
    @IBOutlet weak var label_begin: UILabel!
    @IBOutlet weak var label_subject: UILabel!
    @IBOutlet weak var label_duration: UILabel!
    @IBOutlet weak var label_name: UILabel!
    @IBOutlet weak var label_description: UILabel!
    @IBOutlet weak var label_status: UILabel!

    //Data
    /*
     * public func updateUI(name:String,subject:String,description:String,duration:Int,begin:Date,completion:Double)
     * - Updates UI for data specifed 
     * [Argument]
     * name - name of the task
     * subject - subject of the task
     * description - description of the task
     * duration - duration of the task
     * begin - begin date/time of the task
     * completion - Floating point number 0.0<=x<=1.0, DefinesExtent the task is completed
     *
    */
    public func updateUI(name:String,subject:String,description:String,duration:Int,begin:Date,completion:Double)
    {
        //Init Date Data
        let date = Date()
        let cal = Calendar.autoupdatingCurrent
        var begin_dcmp = cal.dateComponents([Calendar.Component.day, Calendar.Component.month,Calendar.Component.year,Calendar.Component.minute,Calendar.Component.hour], from: begin as Date)
        var diff_dcmp = cal.dateComponents([Calendar.Component.day], from: date, to: begin as Date )

        //Name Label
        self.label_name.text = name

        //Begin Label
        //Adjust Label for Readablity
        switch diff_dcmp.day! {
        case 0:
            label_begin.text = "\(begin_dcmp.hour!) \((begin_dcmp.minute! < 10) ? "0\(begin_dcmp.minute!)":  "\(begin_dcmp.minute!)")"
        case 1:
            label_begin.text = "Tommorow,\(begin_dcmp.hour!) \((begin_dcmp.minute! < 10) ? "0\(begin_dcmp.minute!)":  "\(begin_dcmp.minute!)")"
        default:
            label_begin.text = "\(begin_dcmp.day!)/\(begin_dcmp.month!)/\(begin_dcmp.year!), \(begin_dcmp.hour!) \((begin_dcmp.minute! < 10) ? "0\(begin_dcmp.minute!)":  "\(begin_dcmp.minute!)")"
        }

        //Duration Label
        var drsn_hour = 0
        var drsn_min = 0
        var drsn_left = duration

        drsn_hour = drsn_left / (60 * 60)
        drsn_left -= drsn_hour * (60 * 60)
        drsn_min = drsn_left / (60)

        self.label_duration.text = "\(drsn_hour)h \(drsn_min)m"

        //Status Label
        self.label_status.text = "\(completion * 100)%"
        if completion < 0.50
        {
            self.label_status.textColor = UIColor.red
        }
        else if completion < 1.00
        {
            self.label_status.textColor = UIColor.yellow
        }
        else
        {
            self.label_status.textColor = UIColor.green
        }
    }

    
    //Events
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
