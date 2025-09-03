import { useState, useEffect, useRef } from "react";
import {
    Box,
    Button,
    Container,
    Heading,
    Table,
    Thead,
    Tbody,
    Tr,
    Th,
    Td,
    useDisclosure,
    Modal,
    ModalOverlay,
    ModalContent,
    ModalHeader,
    ModalCloseButton,
    ModalBody,
    ModalFooter,
    FormControl,
    FormLabel,
    Select,
    Checkbox,
    CheckboxGroup,
    Stack,
    HStack,
    useToast,
    IconButton,
    AlertDialog,
    AlertDialogBody,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogContent,
    AlertDialogOverlay,
} from "@chakra-ui/react";
import { LocationDropdown, TransportationApi } from "../api";
import { useNavigate } from "react-router-dom";
import { Plus, Trash2, Pencil } from "lucide-react";

type TransportationRow = {
    id: number;
    originName?: string;
    destinationName?: string;
    type?: string;
    operatingDays?: string | number[];
    [key: string]: any; 
};

export default function TransportationsPage({ showError }: { showError?: (e: string | { title?: string; description?: string; violations?: string[] }) => void }) {
    const [transportations, setTransportations] = useState<TransportationRow[]>([]);
    const { isOpen, onOpen, onClose } = useDisclosure();
    const { isOpen: isDeleteOpen, onOpen: onDeleteOpen, onClose: onDeleteClose } = useDisclosure();

    const [toDeleteId, setToDeleteId] = useState<number | null>(null);
    const { isOpen: isConfirmOpen, onOpen: onConfirmOpen, onClose: onConfirmClose } = useDisclosure();
    const cancelRef = useRef<HTMLButtonElement | null>(null);

    const [locationOptions, setLocationOptions] = useState<{ id: number; name: string; locationCode?: string; city?: string; country?: string }[]>([]);
    const [typeOptions, setTypeOptions] = useState<string[]>([]);

    const [originId, setOriginId] = useState<number | "">("");
    const [destinationId, setDestinationId] = useState<number | "">("");
    const [transportationType, setTransportationType] = useState<string>("");
    const [operatingDays, setOperatingDays] = useState<string[]>([]);

    const [pageNum, setPageNum] = useState(0);
    const [pageSize] = useState(10);
    const [hasNext, setHasNext] = useState(false);
    const [sortBy, setSortBy] = useState<string>("id");
    const [direction, setDirection] = useState<"asc" | "desc">("asc");

    const { isOpen: isEditOpen, onOpen: onEditOpen, onClose: onEditClose } = useDisclosure();
    const { isOpen: isUpdateSuccessOpen, onOpen: onUpdateSuccessOpen, onClose: onUpdateSuccessClose } = useDisclosure();

    type EditForm = {
        id: number;
        originId: number | "";
        destinationId: number | "";
        transportationType: string;
        operatingDays: string[];
    } | null;

    const [editForm, setEditForm] = useState<EditForm>(null);

    const { isOpen: isUpdateConfirmOpen, onOpen: onUpdateConfirmOpen, onClose: onUpdateConfirmClose } = useDisclosure();
    const updateCancelRef = useRef<HTMLButtonElement | null>(null);

    const toast = useToast();
    const navigate = useNavigate();

    const handleDelete = async (id: number) => {
        try {
            await TransportationApi.delete(id);

            const page = await TransportationApi.getAll(pageNum, pageSize, sortBy, direction);
            const items = (page as any).transportations ?? (page as any).content ?? (page as any).items ?? [];
            setTransportations(items);
            if (typeof (page as any).hasNext === "boolean") {
                setHasNext((page as any).hasNext);
            } else if (typeof (page as any).totalElements === "number") {
                const total = (page as any).totalElements as number;
                setHasNext((pageNum + 1) * pageSize < total);
            } else {
                setHasNext(false);
            }

            onDeleteOpen(); 
        } catch (e: any) {
            const detail = e?.response?.data?.detail || e?.response?.data?.message || e?.message || "Delete failed";
            const violations = Array.isArray(e?.response?.data?.violations)
                ? e.response.data.violations.map((v: any) =>
                    v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
                )
                : undefined;
            showError?.({ title: "Delete failed", description: detail, violations });
        }
    };

    useEffect(() => {
        (async () => {
            try {
                const [locs, types, page] = await Promise.all([
                    LocationDropdown.listAllNames(),
                    TransportationApi.getTypes(),
                    TransportationApi.getAll(pageNum, pageSize, sortBy, direction),
                ]);
                setLocationOptions(locs);
                setTypeOptions(types);
                const items = (page as any).transportations ?? (page as any).content ?? (page as any).items ?? [];
                setTransportations(items);
                if (typeof (page as any).hasNext === "boolean") {
                    setHasNext((page as any).hasNext);
                } else if (typeof (page as any).totalElements === "number") {
                    const total = (page as any).totalElements as number;
                    setHasNext((pageNum + 1) * pageSize < total);
                } else {
                    setHasNext(false);
                }
            } catch (e: any) {
                const detail = e?.response?.data?.detail || e?.response?.data?.message || e?.message || "Load failed";
                const violations = Array.isArray(e?.response?.data?.violations)
                    ? e.response.data.violations.map((v: any) =>
                        v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
                    )
                    : undefined;
                showError?.({ title: "Load failed", description: detail, violations });
            }
        })();
    }, [pageNum, pageSize, sortBy, direction]);

    const openEdit = (row: any) => {
        const id = row.id;
        const originId = row.originLocation?.id ?? row.originLocationId ?? "";
        const destinationId = row.destinationLocation?.id ?? row.destinationLocationId ?? "";
        const tType = row.transportationType ?? row.type ?? "";
        const daysArr: string[] = Array.isArray(row.operatingDays)
            ? (row.operatingDays as number[]).map((n) => String(n))
            : typeof row.operatingDays === "string" && row.operatingDays
                ? String(row.operatingDays).split(",").map((s) => s.trim())
                : [];

        setEditForm({
            id,
            originId: originId || "",
            destinationId: destinationId || "",
            transportationType: tType,
            operatingDays: daysArr.length === 7 ? ["ALL"] : daysArr,
        });
        onEditOpen();
    };

    const handleUpdate = async () => {
        if (!editForm) return;
        const { id, originId, destinationId, transportationType, operatingDays } = editForm;
        if (!originId || !destinationId || !transportationType) {
            toast({ status: "warning", title: "Please fill required fields" });
            return;
        }
        try {
            const days =
                operatingDays.length === 1 && operatingDays[0] === "ALL"
                    ? [1, 2, 3, 4, 5, 6, 7]
                    : operatingDays.map((d) => Number(d));

            await TransportationApi.update(id, {
                originLocationId: Number(originId),
                destinationLocationId: Number(destinationId),
                transportationType,
                operatingDays: days,
            });

            const page = await TransportationApi.getAll(pageNum, pageSize, sortBy, direction);
            const items = (page as any).transportations ?? (page as any).content ?? (page as any).items ?? [];
            setTransportations(items);
            if (typeof (page as any).hasNext === "boolean") {
                setHasNext((page as any).hasNext);
            } else if (typeof (page as any).totalElements === "number") {
                const total = (page as any).totalElements as number;
                setHasNext((pageNum + 1) * pageSize < total);
            } else {
                setHasNext(false);
            }

            onEditClose();
            setEditForm(null);
            onUpdateSuccessOpen();
        } catch (e: any) {
            const detail = e?.response?.data?.detail || e?.response?.data?.message || e?.message || "Update failed";
            const violations = Array.isArray(e?.response?.data?.violations)
                ? e.response.data.violations.map((v: any) =>
                    v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
                )
                : undefined;
            showError?.({ title: "Update failed", description: detail, violations });
        }
    };

    return (
        <Container maxW="6xl" py={8}>
            <HStack justify="space-between" mb={4} wrap="wrap" spacing={3}>
                <Heading size="md">Transportations</Heading>
                <HStack>
                    <Select
                        value={sortBy}
                        onChange={(e) => {
                            setPageNum(0);
                            setSortBy(e.target.value);
                        }}
                        size="sm"
                        width="200px"
                        bg="white"
                    >
                        <option value="id">ID</option>
                        <option value="originLocation.name">Origin</option>
                        <option value="destinationLocation.name">Destination</option>
                        <option value="transportationType">Type</option>
                    </Select>

                    <Select
                        value={direction}
                        onChange={(e) => {
                            setPageNum(0);
                            setDirection(e.target.value as "asc" | "desc");
                        }}
                        size="sm"
                        width="120px"
                        bg="white"
                    >
                        <option value="asc">ASC</option>
                        <option value="desc">DESC</option>
                    </Select>

                    <Button
                        leftIcon={<Plus size={16} />}
                        onClick={() => {
                            setOriginId("");
                            setDestinationId("");
                            setTransportationType("");
                            setOperatingDays(["ALL"]);
                            onOpen();
                        }}
                    >
                        Add Transportation
                    </Button>
                </HStack>
            </HStack>

            {}
            <Modal isOpen={isOpen} onClose={onClose}>
                <ModalOverlay />
                <ModalContent>
                    <ModalHeader>Add Transportation</ModalHeader>
                    <ModalCloseButton />
                    <ModalBody>
                        {}
                        <FormControl mb={3} isRequired>
                            <FormLabel>Origin Location</FormLabel>
                            <Select
                                placeholder="Select origin"
                                value={originId}
                                onChange={(e) =>
                                    setOriginId(e.target.value ? Number(e.target.value) : "")
                                }
                            >
                                {locationOptions.map((l) => (
                                    <option key={l.id} value={l.id}>
                                        ({l.locationCode}) {l.name}, {l.city}, {l.country}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>

                        {}
                        <FormControl mb={3} isRequired>
                            <FormLabel>Destination Location</FormLabel>
                            <Select
                                placeholder="Select destination"
                                value={destinationId}
                                onChange={(e) =>
                                    setDestinationId(e.target.value ? Number(e.target.value) : "")
                                }
                            >
                                {locationOptions.map((l) => (
                                    <option key={l.id} value={l.id}>
                                        ({l.locationCode}) {l.name}, {l.city}, {l.country}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>

                        {}
                        <FormControl mb={3} isRequired>
                            <FormLabel>Transportation Type</FormLabel>
                            <Select
                                placeholder="Select type"
                                value={transportationType}
                                onChange={(e) => setTransportationType(e.target.value)}
                            >
                                {typeOptions.map((t) => (
                                    <option key={t} value={t}>
                                        {t}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>

                        {}
                        <FormControl mb={3}>
                            <FormLabel>Operating Days</FormLabel>
                            <CheckboxGroup
                                value={operatingDays}
                                onChange={(vals) => {
                                    const next = vals as string[];
                                    const hadAll = operatingDays.includes("ALL");
                                    const hasAll = next.includes("ALL");

                                    if (hasAll && next.length > 1) {
                                        if (hadAll) {
                                            setOperatingDays(next.filter((v) => v !== "ALL"));
                                        } else {
                                            setOperatingDays(["ALL"]);
                                        }
                                    } else if (hasAll) {
                                        setOperatingDays(["ALL"]);
                                    } else {
                                        setOperatingDays(next);
                                    }
                                }}
                            >
                                <Stack direction="row" wrap="wrap" gap={3}>
                                    <Checkbox value="ALL">All Days</Checkbox>
                                    <Checkbox value="1">Monday</Checkbox>
                                    <Checkbox value="2">Tuesday</Checkbox>
                                    <Checkbox value="3">Wednesday</Checkbox>
                                    <Checkbox value="4">Thursday</Checkbox>
                                    <Checkbox value="5">Friday</Checkbox>
                                    <Checkbox value="6">Saturday</Checkbox>
                                    <Checkbox value="7">Sunday</Checkbox>
                                </Stack>
                            </CheckboxGroup>
                        </FormControl>
                    </ModalBody>
                    <ModalFooter>
                        <Button
                            colorScheme="red"
                            mr={3}
                            onClick={async () => {
                                if (!originId || !destinationId || !transportationType) {
                                    alert("Please fill required fields");
                                    return;
                                }
                                try {
                                    const daysArr =
                                        operatingDays.length === 1 && operatingDays[0] === "ALL"
                                            ? [1, 2, 3, 4, 5, 6, 7]
                                            : operatingDays.map((d) => Number(d));

                                    await TransportationApi.create({
                                        originLocationId: Number(originId),
                                        destinationLocationId: Number(destinationId),
                                        transportationType,
                                        operatingDays: daysArr,
                                    });

                                    const page = await TransportationApi.getAll(pageNum, pageSize, sortBy, direction);
                                    const items = (page as any).transportations ?? (page as any).content ?? (page as any).items ?? [];
                                    setTransportations(items);
                                    if (typeof (page as any).hasNext === "boolean") {
                                        setHasNext((page as any).hasNext);
                                    } else if (typeof (page as any).totalElements === "number") {
                                        const total = (page as any).totalElements as number;
                                        setHasNext((pageNum + 1) * pageSize < total);
                                    }

                                    onClose();
                                    toast({ status: "success", title: "Transportation created" });
                                } catch (e: any) {
                                    const detail = e?.response?.data?.detail || e?.response?.data?.message || e?.message || "Create failed";
                                    const violations = Array.isArray(e?.response?.data?.violations)
                                        ? e.response.data.violations.map((v: any) =>
                                            v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
                                        )
                                        : undefined;
                                    showError?.({ title: "Create failed", description: detail, violations });
                                }
                            }}
                        >
                            Save
                        </Button>
                        <Button variant="ghost" onClick={onClose}>
                            Cancel
                        </Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>

            {}
            <Modal isOpen={isEditOpen} onClose={() => { onEditClose(); setEditForm(null); }}>
                <ModalOverlay />
                <ModalContent>
                    <ModalHeader>Edit Transportation</ModalHeader>
                    <ModalCloseButton />
                    <ModalBody>
                        <FormControl mb={3} isRequired>
                            <FormLabel>Origin Location</FormLabel>
                            <Select
                                placeholder="Select origin"
                                value={editForm?.originId ?? ""}
                                onChange={(e) =>
                                    setEditForm((prev) =>
                                        prev ? { ...prev, originId: e.target.value ? Number(e.target.value) : "" } : prev
                                    )
                                }
                            >
                                {locationOptions.map((l: any) => (
                                    <option key={l.id} value={l.id}>
                                        ({l.locationCode}) {l.name}, {l.city}, {l.country}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl mb={3} isRequired>
                            <FormLabel>Destination Location</FormLabel>
                            <Select
                                placeholder="Select destination"
                                value={editForm?.destinationId ?? ""}
                                onChange={(e) =>
                                    setEditForm((prev) =>
                                        prev ? { ...prev, destinationId: e.target.value ? Number(e.target.value) : "" } : prev
                                    )
                                }
                            >
                                {locationOptions.map((l: any) => (
                                    <option key={l.id} value={l.id}>
                                        ({l.locationCode}) {l.name}, {l.city}, {l.country}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl mb={3} isRequired>
                            <FormLabel>Transportation Type</FormLabel>
                            <Select
                                placeholder="Select type"
                                value={editForm?.transportationType ?? ""}
                                onChange={(e) =>
                                    setEditForm((prev) => (prev ? { ...prev, transportationType: e.target.value } : prev))
                                }
                            >
                                {typeOptions.map((t) => (
                                    <option key={t} value={t}>
                                        {t}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>

                        <FormControl mb={3}>
                            <FormLabel>Operating Days</FormLabel>
                            <CheckboxGroup
                                value={editForm?.operatingDays ?? []}
                                onChange={(vals) => {
                                    const next = vals as string[];
                                    const hadAll = (editForm?.operatingDays ?? []).includes("ALL");
                                    const hasAll = next.includes("ALL");

                                    if (hasAll && next.length > 1) {
                                        if (hadAll) {
                                            setEditForm((prev) => (prev ? { ...prev, operatingDays: next.filter((v) => v !== "ALL") } : prev));
                                        } else {
                                            setEditForm((prev) => (prev ? { ...prev, operatingDays: ["ALL"] } : prev));
                                        }
                                    } else if (hasAll) {
                                        setEditForm((prev) => (prev ? { ...prev, operatingDays: ["ALL"] } : prev));
                                    } else {
                                        setEditForm((prev) => (prev ? { ...prev, operatingDays: next } : prev));
                                    }
                                }}
                            >
                                <Stack direction="row" wrap="wrap" gap={3}>
                                    <Checkbox value="ALL">All Days</Checkbox>
                                    <Checkbox value="1">Monday</Checkbox>
                                    <Checkbox value="2">Tuesday</Checkbox>
                                    <Checkbox value="3">Wednesday</Checkbox>
                                    <Checkbox value="4">Thursday</Checkbox>
                                    <Checkbox value="5">Friday</Checkbox>
                                    <Checkbox value="6">Saturday</Checkbox>
                                    <Checkbox value="7">Sunday</Checkbox>
                                </Stack>
                            </CheckboxGroup>
                        </FormControl>
                    </ModalBody>
                    <ModalFooter>
                        <Button variant="ghost" onClick={() => { onEditClose(); setEditForm(null); }}>Cancel</Button>
                        <Button colorScheme="blue" onClick={onUpdateConfirmOpen}>Save</Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>

            {}
            <Modal isOpen={isDeleteOpen} onClose={onDeleteClose} isCentered>
                <ModalOverlay />
                <ModalContent>
                    <ModalHeader>Transportation Deleted</ModalHeader>
                    <ModalCloseButton />
                    <ModalBody>The transportation has been deleted successfully.</ModalBody>
                    <ModalFooter>
                        <Button
                            colorScheme="blue"
                            onClick={() => {
                                onDeleteClose();
                                navigate("/transportations");
                            }}
                        >
                            OK
                        </Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>
            {}
            <AlertDialog
                isOpen={isUpdateConfirmOpen}
                leastDestructiveRef={updateCancelRef}
                onClose={onUpdateConfirmClose}
                isCentered
            >
                <AlertDialogOverlay />
                <AlertDialogContent>
                    <AlertDialogHeader fontSize="lg" fontWeight="bold">Update Transportation</AlertDialogHeader>
                    <AlertDialogBody>Are you sure you want to save these changes?</AlertDialogBody>
                    <AlertDialogFooter>
                        <Button ref={updateCancelRef} onClick={onUpdateConfirmClose}>Cancel</Button>
                        <Button colorScheme="blue" ml={3} onClick={async () => { await handleUpdate(); onUpdateConfirmClose(); }}>
                            Confirm
                        </Button>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>

            {}
            <Modal isOpen={isUpdateSuccessOpen} onClose={onUpdateSuccessClose} isCentered>
                <ModalOverlay />
                <ModalContent>
                    <ModalHeader>Transportation Updated</ModalHeader>
                    <ModalCloseButton />
                    <ModalBody>The transportation has been updated successfully.</ModalBody>
                    <ModalFooter>
                        <Button colorScheme="blue" onClick={() => { onUpdateSuccessClose(); navigate("/transportations"); }}>
                            OK
                        </Button>
                    </ModalFooter>
                </ModalContent>
            </Modal>

            {}
            <AlertDialog
                isOpen={isConfirmOpen}
                leastDestructiveRef={cancelRef}
                onClose={() => {
                    onConfirmClose();
                    setToDeleteId(null);
                }}
                isCentered
            >
                <AlertDialogOverlay />
                <AlertDialogContent>
                    <AlertDialogHeader fontSize="lg" fontWeight="bold">
                        Delete Transportation
                    </AlertDialogHeader>

                    <AlertDialogBody>
                        Are you sure you want to delete this transportation? This action cannot be undone.
                    </AlertDialogBody>

                    <AlertDialogFooter>
                        <Button ref={cancelRef} onClick={() => {
                            onConfirmClose();
                            setToDeleteId(null);
                        }}>
                            Cancel
                        </Button>
                        <Button colorScheme="red" ml={3} onClick={async () => {
                            if (toDeleteId == null) return;
                            await handleDelete(toDeleteId);
                            onConfirmClose();
                            setToDeleteId(null);
                        }}>
                            Delete
                        </Button>
                    </AlertDialogFooter>
                </AlertDialogContent>
            </AlertDialog>

            <Box border="1px" borderColor="gray.200" borderRadius="md" bg="white" overflowX="auto">
                <Table size="sm" variant="simple">
                    <Thead bg="gray.50">
                        <Tr>
                            <Th>ID</Th>
                            <Th>Origin</Th>
                            <Th>Destination</Th>
                            <Th>Type</Th>
                            <Th>Operating Days</Th>
                            <Th>Actions</Th>
                        </Tr>
                    </Thead>
                    <Tbody>
                        {transportations.map((t) => (
                            <Tr key={t.id}>
                                <Td>{t.id}</Td>
                                <Td>
                                    {t.originLocation
                                        ? `(${t.originLocation.locationCode}) ${t.originLocation.name}, ${t.originLocation.city}, ${t.originLocation.country}`
                                        : t.originLocationName ?? t.originName ?? "-"}
                                </Td>
                                <Td>
                                    {t.destinationLocation
                                        ? `(${t.destinationLocation.locationCode}) ${t.destinationLocation.name}, ${t.destinationLocation.city}, ${t.destinationLocation.country}`
                                        : t.destinationLocationName ?? t.destinationName ?? "-"}
                                </Td>
                                <Td>{t.transportationType ?? t.type ?? "-"}</Td>
                                <Td>
                                    {Array.isArray(t.operatingDays)
                                        ? (t.operatingDays as number[]).join(",")
                                        : (t.operatingDays ?? "-")}
                                </Td>
                                <Td>
                                    <HStack justify="flex-start" spacing={2}>
                                        <IconButton
                                            aria-label="edit"
                                            size="sm"
                                            variant="outline"
                                            onClick={() => openEdit(t)}
                                            icon={<Pencil size={16} />}
                                        />
                                        <IconButton
                                            aria-label="delete"
                                            size="sm"
                                            variant="outline"
                                            onClick={() => {
                                                setToDeleteId(t.id);
                                                onConfirmOpen();
                                            }}
                                            icon={<Trash2 size={16} />}
                                        />
                                    </HStack>
                                </Td>
                            </Tr>
                        ))}
                    </Tbody>
                </Table>
            </Box>

            <HStack mt={4} justify="space-between">
                <Button
                    onClick={() => setPageNum((p) => Math.max(p - 1, 0))}
                    isDisabled={pageNum === 0}
                    variant="outline"
                >
                    Previous
                </Button>
                <Box>Page {pageNum + 1}</Box>
                <Button
                    onClick={() => setPageNum((p) => p + 1)}
                    isDisabled={!hasNext}
                    variant="outline"
                >
                    Next
                </Button>
            </HStack>
        </Container>
    );
}
