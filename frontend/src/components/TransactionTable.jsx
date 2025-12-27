import { useState, useMemo, useEffect } from 'react';
import {
    useReactTable,
    getCoreRowModel,
    flexRender,
    getPaginationRowModel,
} from '@tanstack/react-table';
import { Search, ChevronLeft, ChevronRight } from 'lucide-react';

const TransactionTable = () => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');

    // Fetch logic (Debounce needed in prod, keeping simple)
    useEffect(() => {
        // Construct query
        const params = new URLSearchParams();
        if (search) params.append('gatewayReference', search);

        fetch(`/api/v1/payments/search?${params.toString()}`)
            .then(res => res.json())
            .then(page => {
                setData(page.content || []); // Standard Spring Data JSON
                setLoading(false);
            })
            .catch(err => console.error(err));
    }, [search]);

    const columns = useMemo(() => [
        {
            header: 'Gateway Ref',
            accessorKey: 'gatewayReference',
            cell: info => <span className="font-mono text-slate-700">{info.getValue()}</span>
        },
        {
            header: 'Amount',
            accessorKey: 'amount',
            cell: info => <span className="font-medium">${info.getValue()}</span>
        },
        {
            header: 'Currency',
            accessorKey: 'currency',
        },
        {
            header: 'Date',
            accessorKey: 'transactionDate',
        },
        {
            header: 'Gateway',
            accessorKey: 'gatewayProvider',
        }
    ], []);

    const table = useReactTable({
        data,
        columns,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
    });

    return (
        <div className="space-y-4">
            <div className="flex justify-between items-center">
                <h2 className="text-xl font-bold text-slate-900">Transactions</h2>
                <div className="relative">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                    <input
                        type="text"
                        placeholder="Search Gateway Ref..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                        className="pl-10 pr-4 py-2 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
            </div>

            <div className="bg-white rounded-lg shadow-sm border border-slate-200 overflow-hidden">
                <table className="w-full text-left">
                    <thead className="bg-slate-50 border-b border-slate-200">
                        {table.getHeaderGroups().map(headerGroup => (
                            <tr key={headerGroup.id}>
                                {headerGroup.headers.map(header => (
                                    <th key={header.id} className="px-6 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wider">
                                        {flexRender(header.column.columnDef.header, header.getContext())}
                                    </th>
                                ))}
                            </tr>
                        ))}
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                        {loading ? (
                            <tr><td colSpan={5} className="p-8 text-center text-slate-500">Loading...</td></tr>
                        ) : data.length === 0 ? (
                            <tr><td colSpan={5} className="p-8 text-center text-slate-500">No transactions found</td></tr>
                        ) : (
                            table.getRowModel().rows.map(row => (
                                <tr key={row.id} className="hover:bg-slate-50 transition-colors">
                                    {row.getVisibleCells().map(cell => (
                                        <td key={cell.id} className="px-6 py-4 text-sm text-slate-600">
                                            {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                        </td>
                                    ))}
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {/* Simple Pagination Controls */}
            <div className="flex justify-end gap-2">
                <button
                    onClick={() => table.previousPage()}
                    disabled={!table.getCanPreviousPage()}
                    className="p-2 border rounded hover:bg-slate-50 disabled:opacity-50"
                >
                    <ChevronLeft size={16} />
                </button>
                <button
                    onClick={() => table.nextPage()}
                    disabled={!table.getCanNextPage()}
                    className="p-2 border rounded hover:bg-slate-50 disabled:opacity-50"
                >
                    <ChevronRight size={16} />
                </button>
            </div>
        </div>
    );
};

export default TransactionTable;
