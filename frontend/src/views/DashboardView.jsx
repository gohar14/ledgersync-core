import { useEffect, useState } from 'react';
import { TrendingUp, AlertCircle, CheckCircle2, DollarSign } from 'lucide-react';

const StatCard = ({ title, value, subtext, icon: Icon, color }) => (
    <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-100">
        <div className="flex justify-between items-start">
            <div>
                <p className="text-sm font-medium text-slate-500">{title}</p>
                <h3 className="text-2xl font-bold text-slate-900 mt-1">{value}</h3>
            </div>
            <div className={`p-2 rounded-lg bg-${color}-50 text-${color}-600`}>
                <Icon size={20} />
            </div>
        </div>
        {subtext && <p className="text-xs text-slate-400 mt-2">{subtext}</p>}
    </div>
);

const DashboardView = () => {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch('/api/v1/dashboard/stats')
            .then(res => res.json())
            .then(data => {
                setStats(data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Failed to fetch stats", err);
                setLoading(false);
            });
    }, []);

    if (loading) return <div className="p-8">Loading dashboard...</div>;

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h1 className="text-2xl font-bold text-slate-900">Overview</h1>
                <div className="text-sm text-slate-500">Last updated: Just now</div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <StatCard
                    title="Total Processed"
                    value={stats?.totalProcessed?.toLocaleString() || '0'}
                    icon={Activity}
                    color="blue"
                    subtext="Total transactions ingested"
                />
                <StatCard
                    title="Matching Rate"
                    value={stats?.matchRatePercentage || '0%'}
                    icon={CheckCircle2}
                    color="emerald"
                    subtext="Overall reconciliation success"
                />
                <StatCard
                    title="Unmatched Amount"
                    value={`$${stats?.unmatchedAmount?.toLocaleString() || '0.00'}`}
                    icon={AlertCircle}
                    color="rose"
                    subtext="Requires attention"
                />
                <StatCard
                    title="Total Fees Computed"
                    value={`$${stats?.totalFeesComputed?.toLocaleString() || '0.00'}`}
                    icon={DollarSign}
                    color="amber"
                    subtext="Revenue impact"
                />
            </div>
        </div>
    );
};

// Fix for icon usage (Activity was not imported)
import { Activity } from 'lucide-react';

export default DashboardView;
